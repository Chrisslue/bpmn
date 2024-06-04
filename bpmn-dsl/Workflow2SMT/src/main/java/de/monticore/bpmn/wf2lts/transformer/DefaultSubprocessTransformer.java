package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.scopes.SubProcessScope;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._util.WorkflowTypeDispatcher;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSubprocessTransformer implements SubprocessTransformer {

  private static void collectAndReplaceStartEvents(
      ParameterPack parameterPack,
      Transition oldTransition,
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
                            .isWorkflowASTEventTriggerTerminate(event.getTrigger()));

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
                            .isWorkflowASTEventTriggerTerminate(event.getTrigger()));

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
      Stream<ASTEvent> events, NamingStrategy<ASTFlowNode> namingStrategy) {
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
      NamingStrategy<ASTFlowNode> namingStrategy,
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

    private final NamingStrategy<ASTFlowNode> namingStrategy;

    private final SubProcessScope subProcessScope;

    private final String newStartName;

    private final String newEndName;

    public ParameterPack(
        NamingStrategy<ASTFlowNode> namingStrategy,
        SubProcessScope subProcessScope,
        String newStartName,
        String newEndName) {
      this.namingStrategy = namingStrategy;
      this.subProcessScope = subProcessScope;
      this.newStartName = newStartName;
      this.newEndName = newEndName;
    }

    public NamingStrategy<ASTFlowNode> getNamingStrategy() {
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
