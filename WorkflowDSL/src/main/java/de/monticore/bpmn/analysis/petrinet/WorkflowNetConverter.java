/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.analysis.petrinet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.bpmn.analysis.petrinet.modules.*;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.visitors.WorkflowLocalVisitor;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import petrinet._ast.ASTPetriNode;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

/**
 * Translates a BPMN process xor sub-process into a WF-net.
 *
 * <p>The translation handles a single process level at a time. Contained sub-processes must be
 * translated separately.
 *
 * <p>The WF-net does not preserve the BPMN execution semantics if the process xor sub-process
 * contains one of: - diverging ior gateway (we do not know which branches should be synchronized
 * later on, only known at runtime) - more than one end event (we do not know which end events
 * should be synchronized) - terminate events - compensate events - sub-process boundary events
 *
 * <p>Based on
 * https://svn.win.tue.nl/trac/prom/browser/Packages/BPMNConversions/Trunk/src/org/processmining/plugins/converters/bpmn2pn/BPMN2PetriNetConverter.java
 */
public class WorkflowNetConverter extends WorkflowLocalVisitor {
  
  private static final List<Class> UNSUPPORTED_TRIGGERS = Lists.newArrayList(
      ASTWFEventTriggerCompensate.class, ASTWFEventTriggerTerminate.class,
      ASTWFEventTriggerNotification.class, // TODO only unsupported if intermediate xor end
      ASTWFEventTriggerCancel.class);
  
  private static final Predicate<ASTWFEvent> isSupportedTrigger = event -> {
    if (event.isPresentTrigger()) {
      return UNSUPPORTED_TRIGGERS.stream().noneMatch(unsopported -> unsopported.isInstance(event
          .getTrigger()));
    }
    return true;
  };
  
  private final Random random = new Random();
  
  private ASTPetrinet petrinet;
  private ASTPlace source;
  private ASTPlace sink;
  
  private List<ASTPlace> startPlaces = Lists.newArrayList();
  private List<ASTPlace> endPlaces = Lists.newArrayList();
  
  private Map<SequenceFlow, ASTPlace> flow2Place;
  private Map<ASTFlowElement, PetriNetModule<? extends ASTFlowElement>> modules;
  
  private List<Warning> warnings;
  
  public WorkflowNetConverter(final ASTWFProcess localRoot) {
    super(localRoot);
  }
  
  public WorkflowNet convert() {
    petrinet = PetriNetFactory.createEmptyPetriNet(((ASTWFProcess) localRoot).getName());
    flow2Place = Maps.newHashMap();
    modules = Maps.newHashMap();
    
    warnings = Lists.newArrayList();
    
    startPlaces = Lists.newArrayList();
    endPlaces = Lists.newArrayList();
    
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(this);
    localRoot.accept(traverser);
    
    return new WorkflowNet(petrinet, source, sink, warnings, getMapping());
  }
  
  @Override
  public void visit(final ASTWFProcess container) {
    // sequence flow has to be handled before all other nodes
    if (localRoot == container) {
      handleSequenceFlow();
    }
  }
  
  @Override
  public void endVisit(final ASTWFProcess container) {
    if (localRoot == container) {
      makeSourceAndSink();
    }
  }
  
  @Override
  public void endVisit(final ASTWFSubProcess subProcess) {
    if (localRoot == subProcess) { // do not handle local root
      return;
    }
    /*
    if (subProcess.getSymbol().isTriggeredByEvent()) { // event sub-process is not not part of normal flow
      return;
    }
    */
    handleSubProcess(subProcess);
  }
  
  @Override
  public void endVisit(final ASTWFActivity activity) {
    if (activity instanceof ASTWFCallActivity || activity instanceof ASTWFTask) { // compensation activity is not part of normal flow
      if (activity.getSymbol().isCompensating()) {
        return;
      }
    }
    
    handleActivity(activity);
  }
  
  @Override
  public void endVisit(final ASTWFGateway gateway) {
    handleGateway(gateway);
  }
  
  @Override
  public void endVisit(final ASTWFEvent event) {
    if (isBoundaryCompensationEvent(event)) { // compensation boundary events are not translated
      return;
    }
    
    handleEvent(event);
  }
  
  private void makeSourceAndSink() {
    source = addPlace("p_source_" + ((ASTWFProcess) localRoot).getName());
    startPlaces.forEach(start -> {
      ASTTransition t = addTransition("t_source_" + random());
      connect(source, t);
      connect(t, start);
    });
    
    sink = addPlace("p_sink_" + ((ASTWFProcess) localRoot).getName());
    endPlaces.forEach(end -> {
      ASTTransition t = addTransition("t_sink_" + random());
      connect(end, t);
      connect(t, sink);
    });
    
    // in case the net is empty connect source to sink via an extra transition
    if (startPlaces.isEmpty() && endPlaces.isEmpty()) {
      ASTTransition t = addTransition("t_source_sink_" + random());
      connect(source, t);
      connect(t, sink);
    }
  }
  
  private ASTPlace makeStartPlace(final ASTFlowElement flowNode) {
    ASTPlace start = addPlace("p_start_" + flowNode.getName());
    startPlaces.add(start);
    
    return start;
  }
  
  private ASTPlace makeEndPlace(final ASTFlowElement flowNode) {
    ASTPlace end = addPlace("p_end_" + flowNode.getName());
    endPlaces.add(end);
    
    return end;
  }
  
  private Map<ASTPetriNode, Set<ASTFlowElement>> getMapping() {
    return Stream.concat(modules.entrySet().stream().flatMap(e -> Stream.concat(e.getValue()
        .getPlaces().stream().map(v -> Maps.immutableEntry(v, e.getKey())), e.getValue()
            .getTransitions().stream().map(v -> Maps.immutableEntry(v, e.getKey())))), flow2Place
                .entrySet().stream().map(e -> Maps.immutableEntry(e.getValue(), e.getKey()
                    .getTarget()))).collect(Collectors.toMap(Map.Entry::getKey, e -> Sets
                        .newHashSet(e.getValue()), this::mergeSets));
  }
  
  private void handleSequenceFlow() {
    // group sequence flows by target node
    Map<ASTFlowElement, List<SequenceFlow>> targets = WorkflowCollectors.toSequenceFlowLocal(
        ((ASTWFProcess) localRoot)).stream().collect(Collectors.toMap(SequenceFlow::getTarget,
            Lists::newArrayList, this::mergeLists));
    
    // handle "uncontrolled flow" (see OMG 13.3.1: Sequence Flow Considerations)
    Maps.filterEntries(targets, this::isUncontrolledFlow).values().forEach(flows -> {
      ASTPlace p = addPlace("p_flow_merge_" + flows.get(0).getTargetName());
      flows.forEach(f -> flow2Place.put(f, p));
    });
    
    Maps.filterEntries(targets, e -> null != e && !isUncontrolledFlow(e)).values().stream().flatMap(
        Collection::stream).forEach(f -> {
          ASTPlace p = addPlace("p_flow_" + f.getSourceName() + "_" + f.getTargetName());
          flow2Place.put(f, p);
        });
  }
  
  private boolean isUncontrolledFlow(final Map.Entry<ASTFlowElement, List<SequenceFlow>> entry) {
    return entry != null && !(entry.getKey() instanceof ASTWFGateway) && entry.getValue().size()
        > 1;
  }
  
  private void handleActivity(final ASTWFActivity task) {
    if (task instanceof ASTWFTask) {
      handleActivity(task, ((ASTWFTask) task).getBoundaryEventList());
    }
    
    if (task instanceof ASTWFCallActivity) {
      handleActivity(task, ((ASTWFCallActivity) task).getBoundaryEventList());
    }
    
  }
  
  /**
   * Add sub-process as activity to the workflow net. Sub-process must be checked recursively.
   *
   * @param subProcess
   */
  private void handleSubProcess(final ASTWFSubProcess subProcess) {
    handleActivity(subProcess, WorkflowCollectors.toEventsLocalSubProcess(subProcess));
  }
  
  private void handleActivity(final ASTWFActivity activity,
      final List<? extends ASTWFEvent> boundaryEvents) {
    List<EventModule> boundaryModules = boundaryEvents.stream().filter(
        event -> !isBoundaryCompensationEvent(event)).map(modules::get).map(EventModule.class::cast)
        .collect(Collectors.toList());
    
    ActivityModule module = new ActivityModule(activity, boundaryModules, getInputPlaces(activity),
        getOutputPlaces(activity));
    
    addModule(module);
  }
  
  private void handleGateway(final ASTWFGateway gateway) {
    GatewayModule module;
    
    ASTGatewayType type = gateway.getType();
    if (type.isExclusive() || type.isExclusiveEventBased()) {
      module = new ExclusiveGatewayModule(gateway, getInputPlaces(gateway), getOutputPlaces(
          gateway));
    }
    else if (type.isInclusive() || type.isComplex()) {
      module = new InclusiveGatewayModule(gateway, getInputPlaces(gateway), getOutputPlaces(
          gateway));
    }
    else {
      module = new ParallelGatewayModule(gateway, getInputPlaces(gateway), getOutputPlaces(
          gateway));
    }
    
    addModule(module);
  }
  
  private void handleEvent(final ASTWFEvent event) {
    if (!isSupportedTrigger.test(event)) {
      warnings.add(new Warning(Warning.Type.UNSOUND_TRANSLATION, event));
    }
    
    EventModule module;
    if (event.isStart()) {
      ASTPlace p = makeStartPlace(event);
      module = new EventModule(event, Lists.newArrayList(p), getOutputPlaces(event));
    }
    else if (event.isEnd()) {
      ASTPlace p = makeEndPlace(event);
      module = new EventModule(event, getInputPlaces(event), Lists.newArrayList(p));
    }
    else if (event.getSymbol().isBoundary()) {
      module = new EventModule(event, Lists.newArrayList(), getOutputPlaces(event));
    }
    else {
      module = new EventModule(event, getInputPlaces(event), getOutputPlaces(event));
    }
    
    addModule(module);
  }
  
  private boolean isBoundaryCompensationEvent(final ASTWFEvent event) {
    return event.getSymbol().isBoundary() && event.isPresentTrigger() && WorkflowFilters
        .isCompensateTrigger(event.getTrigger());
  }
  
  private ASTPlace addPlace(final String name) {
    ASTPlace place = PetriNetFactory.createPlace(name);
    petrinet.addPlace(place);
    
    return place;
  }
  
  private ASTTransition addTransition(final String name) {
    ASTTransition transition = PetriNetFactory.createTransition(name);
    petrinet.addTransition(transition);
    
    return transition;
  }
  
  private void addModule(final PetriNetModule<? extends ASTFlowElement> module) {
    modules.put(module.getFlowNode(), module);
    
    petrinet.addAllPlaces(module.getPlaces());
    petrinet.addAllTransitions(module.getTransitions());
  }
  
  private void connect(final ASTPlace place, final ASTTransition transition) {
    PetriNetUtils.connect(place, transition);
  }
  
  private void connect(final ASTTransition transition, final ASTPlace place) {
    PetriNetUtils.connect(transition, place);
  }
  
  /**
   * Returns the places corresponding to the incoming sequence flow of {@code flowNode} if any xor
   * creates a new start place
   *
   * @param flowNode
   * @return
   */
  private List<ASTPlace> getInputPlaces(final ASTFlowElement flowNode) {
    List<ASTPlace> inputPlaces = flowNode.streamIncomings().map(flow2Place::get).distinct().collect(
        Collectors.toList());
    return inputPlaces.isEmpty() ? Lists.newArrayList(makeStartPlace(flowNode)) : inputPlaces;
  }
  
  /**
   * Returns the places corresponding to the outgoing sequence flow of {@code flowNode} if any xor
   * creates a new end place
   *
   * @param flowNode
   * @return
   */
  private List<ASTPlace> getOutputPlaces(final ASTFlowElement flowNode) {
    List<ASTPlace> outputPlaces = flowNode.streamOutgoings().map(flow2Place::get).distinct()
        .collect(Collectors.toList());
    return outputPlaces.isEmpty() ? Lists.newArrayList(makeEndPlace(flowNode)) : outputPlaces;
  }
  
  private <U> List<U> mergeLists(final List<U> list1, final List<U> list2) {
    return Lists.newArrayList(Iterables.concat(list1, list2));
  }
  
  private <U> Set<U> mergeSets(final Set<U> set1, final Set<U> set2) {
    return Sets.newHashSet(Iterables.concat(set1, set2));
  }
  
  private int random() {
    return random.nextInt() & Integer.MAX_VALUE;
  }
  
  public static class Warning {
    
    private final Type type;
    
    private final ASTFlowElement node;
    
    Warning(final Type type, final ASTFlowElement node) {
      this.type = type;
      this.node = node;
    }
    
    public Type getType() { return type; }
    
    public ASTFlowElement getNode() { return node; }
    
    public enum Type {
      INNER_DETAILS_IGNORED, UNSOUND_TRANSLATION,
    }
    
  }
  
}
