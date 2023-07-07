package de.monticore.wf2lts.transformer;

import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.bpmn.workflow._util.WorkflowTypeDispatcher;
import de.monticore.wf2lts.NamingStrategy;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.scopes.SubProcessScope;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSubprocessTransformer implements SubprocessTransformer {

  private static void collectAndReplaceStartEvents(
      ParameterPack parameterPack,
      LTS.Transition oldTransition,
      LTS internalGraph,
      LTS externalGraph) {

    var internalStartNames =
        getEventNames(
            parameterPack.getSubProcessScope().getStartEvents().stream(),
            parameterPack.getNamingStrategy());
    replaceInternalStartEvents(
        internalGraph,
        externalGraph,
        internalStartNames,
        parameterPack.getNewStartName(),
        oldTransition.getSource(),
        oldTransition.getConditions());
  }

  private static void replaceInternalStartEvents(
      LTS internalGraph,
      LTS externalGraph,
      List<String> startEventNames,
      String startName,
      LTS.State externalSource,
      List<ASTFlowCondition> oldConditions) {
    startEventNames.stream()
        .map(internalGraph::getTransitionsForLabel)
        .flatMap(List::stream)
        .collect(Collectors.toList())
        .stream()
        .map(
            transition ->
                replaceInternalStart(
                    internalGraph, transition, startName, externalSource, oldConditions))
        .collect(Collectors.toList())
        .forEach(externalGraph::addTransition);
  }

  private static LTS.Transition replaceInternalStart(
      LTS internalGraph,
      LTS.Transition startEventTransition,
      String startName,
      LTS.State externalSource,
      List<ASTFlowCondition> oldConditions) {
    internalGraph.removeTransition(startEventTransition);
    return startEventTransition
        .changedLabel(startName)
        .changedSource(externalSource)
        .withAddedConditions(oldConditions);
  }

  private static void collectAndReplaceEndEvents(
      ParameterPack parameterPack, LTS.Transition oldTransition, LTS internalGraph) {
    var plainEndEvents =
        parameterPack.getSubProcessScope().getEndEvents().stream()
            .filter(
                event ->
                    !event.isPresentTrigger()
                        || !new WorkflowTypeDispatcher()
                            .isASTEventTriggerTerminate(event.getTrigger()));

    var internalEndNames = getEventNames(plainEndEvents, parameterPack.getNamingStrategy());
    replaceInternalEndEvents(
        internalGraph, internalEndNames, parameterPack.getNewEndName(), oldTransition.getTarget());
  }

  private static void replaceInternalEndEvents(
      LTS internalGraph, List<String> endEventNames, String endName, LTS.State externalTarget) {
    endEventNames.stream()
        .map(internalGraph::getTransitionsForLabel)
        .flatMap(List::stream)
        .collect(Collectors.toList())
        .forEach(
            transition -> replaceInternalEnd(internalGraph, transition, endName, externalTarget));
  }

  private static void replaceInternalEnd(
      LTS internalGraph,
      LTS.Transition endEventTransition,
      String endName,
      LTS.State externalTarget) {
    internalGraph.removeTransition(endEventTransition);
    internalGraph.addTransition(
        endEventTransition.changedLabel(endName).changedTarget(externalTarget));
  }

  private static void collectAndReplaceTerminatingEvents(
      ParameterPack parameterPack, LTS internalGraph, List<LTS.Transition> subProcessOutgoings) {
    var terminatingEvents =
        parameterPack.getSubProcessScope().getEndEvents().stream()
            .filter(
                event ->
                    event.isPresentTrigger()
                        && new WorkflowTypeDispatcher()
                            .isASTEventTriggerTerminate(event.getTrigger()));

    var internalTerminatingNames =
        getEventNames(terminatingEvents, parameterPack.getNamingStrategy());
    replaceInternalTermEvents(internalGraph, internalTerminatingNames, subProcessOutgoings);
  }

  private static void replaceInternalTermEvents(
      LTS internalGraph,
      List<String> terminatingEventNames,
      List<LTS.Transition> subProcessOutgoings) {
    terminatingEventNames.stream()
        .map(internalGraph::getTransitionsForLabel)
        .flatMap(List::stream)
        .forEach(
            terminatingTransition ->
                replaceInternalTerm(terminatingTransition, subProcessOutgoings, internalGraph));
  }

  private static void replaceInternalTerm(
      LTS.Transition terminatingTransition,
      List<LTS.Transition> subprocessOutgoingList,
      LTS internalGraph) {
    internalGraph.removeTransition(terminatingTransition);
    subprocessOutgoingList.forEach(
        subprocessOutgoing ->
            internalGraph.addTransition(
                subprocessOutgoing
                    .changedSource(terminatingTransition.getSource())
                    .withAddedConditions(terminatingTransition.getConditions())));
  }

  private static List<String> getEventNames(
      Stream<ASTEvent> events, NamingStrategy<IFlowNode> namingStrategy) {
    return events.map(namingStrategy).collect(Collectors.toList());
  }

  /**
   * Transform surrounding (external) graph to lts. For all occurrences in the external LTS: -
   * Replace the start-event-transitions with one labeled with startName - Replace the
   * terminating-transitions with the successors of the subprocess. - Replace the
   * end-event-transitions with one labeled with endName
   */
  @Override
  public void transform(
      SubProcessScope subProcessScope,
      LTS externalGraph,
      NamingStrategy<IFlowNode> namingStrategy,
      Graph2LTSTransformer graphTransformer) {
    var processName = namingStrategy.apply(subProcessScope.getSubProcess());
    var parameterPack =
        new ParameterPack(
            namingStrategy, subProcessScope, processName + "_Start", processName + "_End");

    // Convert internalGraph
    var internalGraph = graphTransformer.transform(subProcessScope.getInternalGraph());

    for (var oldSubprocessTransition : externalGraph.getTransitionsForLabel(processName)) {
      collectAndReplaceStartEvents(
          parameterPack, oldSubprocessTransition, internalGraph, externalGraph);
      collectAndReplaceEndEvents(parameterPack, oldSubprocessTransition, internalGraph);
      var subProcessOutgoings = externalGraph.getOutgoings(oldSubprocessTransition.getTarget());
      collectAndReplaceTerminatingEvents(parameterPack, internalGraph, subProcessOutgoings);
      externalGraph.removeTransition(oldSubprocessTransition);
    }
    externalGraph.addTransitionsOf(internalGraph);
  }

  private static class ParameterPack {

    private final NamingStrategy<IFlowNode> namingStrategy;

    private final SubProcessScope subProcessScope;

    private final String newStartName;

    private final String newEndName;

    public ParameterPack(
        NamingStrategy<IFlowNode> namingStrategy,
        SubProcessScope subProcessScope,
        String newStartName,
        String newEndName) {
      this.namingStrategy = namingStrategy;
      this.subProcessScope = subProcessScope;
      this.newStartName = newStartName;
      this.newEndName = newEndName;
    }

    public NamingStrategy<IFlowNode> getNamingStrategy() {
      return namingStrategy;
    }

    public SubProcessScope getSubProcessScope() {
      return subProcessScope;
    }

    public String getNewStartName() {
      return newStartName;
    }

    public String getNewEndName() {
      return newEndName;
    }
  }
}
