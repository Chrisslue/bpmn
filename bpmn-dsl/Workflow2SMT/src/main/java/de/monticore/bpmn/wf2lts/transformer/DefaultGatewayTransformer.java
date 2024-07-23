package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._util.WorkflowTypeDispatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultGatewayTransformer implements GatewayTransformer {

  private final GatewayInterleavingStrategy interleavingStrategy;
  private final NamingStrategy<ASTFlowNode> namingStrategy;

  public DefaultGatewayTransformer(
      GatewayInterleavingStrategy interleavingStrategy,
      NamingStrategy<ASTFlowNode> namingStrategy) {
    this.interleavingStrategy = interleavingStrategy;
    this.namingStrategy = namingStrategy;
  }

  @Override
  public GatewayInterleavingStrategy getGatewayInterleavingStrategy() {
    return interleavingStrategy;
  }

  private LTSWithFinalStates removeInternalSplitAndMerge(
      LTS internalLTS, String splitName, Optional<String> optMergeName) {
    removeInternalSplitTransitions(internalLTS, splitName);
    LTSWithFinalStates ltsWithFinalStates =
        new LTSWithFinalStates(internalLTS, Collections.emptyList());

    // Remove internal merge transitions, for each mark the source state as final state
    optMergeName.ifPresent(
        s ->
            removeInternalMergeTransition(ltsWithFinalStates, s)
                .forEach(
                    mergeTransition ->
                        ltsWithFinalStates.addAsFinalState(mergeTransition.getSource())));
    return ltsWithFinalStates;
  }

  /**
   * Interleaving assumes that the outgoings of the start node are the successors of the
   * split-gateway. Therefore, the splitName transitions have to be removed from the internal lts
   * first.
   *
   * @param internalLTS The internals of the gateway as lts.
   * @param splitName The name of the split-gateway produced by the naming strategy.
   */
  private static void removeInternalSplitTransitions(LTS internalLTS, String splitName) {
    List<Transition> internalSplitting = internalLTS.getTransitionsForLabel(splitName);
    if (internalSplitting.isEmpty()) {
      throw new IllegalStateException(
          "No splitting transitions in lts."
              + "Expected to find"
              + splitName
              + " .In lts: "
              + internalLTS);
    }
    // Validate that all split transitions are sourced from the internal start.
    Optional<Transition> optWitness =
        internalSplitting.stream()
            .filter(splitTransition -> !splitTransition.getSource().equals(internalLTS.getStart()))
            .findFirst();
    if (optWitness.isPresent()) {
      throw new IllegalStateException(
          "Internal split transition had another source as start node."
              + "Expected "
              + internalLTS.getStart()
              + " But source was "
              + optWitness.get().getSource()
              + " for transition"
              + optWitness.get());
    }
    for (var splitTransition : internalSplitting) {
      List<Transition> toBeChanged = internalLTS.getOutgoings(splitTransition.getTarget());
      for (var successorTransitions : toBeChanged) {
        internalLTS.addTransition(
            successorTransitions
                .changedSource(internalLTS.getStart())
                .withAddedConditions(splitTransition.getConditions()));
      }
      toBeChanged.forEach(internalLTS::removeTransition);
    }
    removeTransitionsLabeled(splitName, internalLTS);
  }

  private List<Transition> removeInternalMergeTransition(LTS internalLTS, String mergeName) {
    var mergeTransitions = internalLTS.getTransitionsForLabel(mergeName);
    if (mergeTransitions.stream()
        .anyMatch(transition -> !internalLTS.getOutgoings(transition.getTarget()).isEmpty())) {
      throw new IllegalStateException("Merge transition target had outgoing transitions");
    }
    removeTransitionsLabeled(mergeName, internalLTS);
    return mergeTransitions;
  }

  /**
   * Add all outgoings of internal start to all sources of external transitions labeled with
   * splitName. Assuming splitName is not part of the internal lts anymore.
   *
   * @param externalLTS The lts surrounding the GatewayScope.
   * @param splitName The name of the splitting gateway.
   * @param internalLTS The lts representing all transitions encapsulated by the GatewayScope.
   */
  private static void rewireExternalSplitTransitions(
      LTS externalLTS, String splitName, LTS internalLTS) {
    List<Transition> oldTransitions = externalLTS.getTransitionsForLabel(splitName);
    if (internalLTS.isLabelPresent(splitName)) {
      throw new IllegalStateException("Internal LTS should not contain transitions with splitName");
    }
    for (LTS.Transition oldTransition : oldTransitions) {
      externalLTS.addTransition(
          oldTransition.changedLabel("").changedTarget(internalLTS.getStart()));
    }
  }

  /**
   * For every internal final state, add all outgoings of the external merge transitions.
   *
   * @param externalLTS The lts surrounding the GatewayScope.
   * @param mergeName The name of the merging gateway. Appearing both in the external and internal
   *     lts.
   * @param internalLTS The lts representing everything between split and merge.
   */
  private static void rewireExternalMergeTransitions(
      LTS externalLTS, String mergeName, LTSWithFinalStates internalLTS) {
    List<Transition> externalMerging = externalLTS.getTransitionsForLabel(mergeName);
    if (externalMerging.isEmpty()) {
      throw new IllegalStateException(
          "No merging transitions in external lts. Expected to find "
              + mergeName
              + ". In lts: "
              + externalLTS);
    }

    LTS.State connectorState = new LTS.State();
    internalLTS
        .getFinalStates()
        .forEach(
            state ->
                internalLTS.addTransition(
                    new Transition(state, new ArrayList<>(), "", connectorState)));

    // All outgoing transitions of all external-merging-targets..
    // todo: add epsilon-transition?
    var externalSuccessors =
        externalMerging.stream()
            .map(Transition::getTarget)
            .flatMap(
                externalMergingTarget -> externalLTS.getOutgoings(externalMergingTarget).stream())
            .collect(Collectors.toList());

    externalSuccessors.forEach(
        transition -> externalLTS.addTransition(transition.changedSource(connectorState)));

    rewireExternalIncomingMergeTransitions(externalLTS, mergeName, connectorState);
  }

  private static void rewireExternalIncomingMergeTransitions(
      LTS externalLTS, String mergeName, LTS.State connectorState) {
    // If the mergeName is part of the externalLTS there is possible a cycle.
    // In that case pass incoming transitions to the merge-transition through to the outgoing
    // transitions.
    // For example for every s0 - A -> s1 - Merge -> s2 - b -> s3 rewire it to:
    // s0 - A -> s2 - b -> s3
    // todo: add epsilon-transition?
    for (var externalMergeTransition : externalLTS.getTransitionsForLabel(mergeName)) {
      for (Transition incomingTransition :
          externalLTS.getIncoming(externalMergeTransition.getSource())) {
        externalLTS.removeTransition(incomingTransition);
        externalLTS.addTransition(
            incomingTransition
                .changedTarget(connectorState)
                .withAddedConditions(externalMergeTransition.getConditions()));
      }
    }
  }

  // Remove all transitions with name as label.
  public static void removeTransitionsLabeled(String name, LTS lts) {
    List<Transition> transitionsToBeRemoved = lts.getTransitionsForLabel(name);
    var possiblyDanglingStates =
        transitionsToBeRemoved.stream()
            .flatMap(transition -> Stream.of(transition.getSource(), transition.getTarget()))
            .filter(state -> lts.getStart() != state)
            .collect(Collectors.toList());
    transitionsToBeRemoved.forEach(lts::removeTransition);
    possiblyDanglingStates.stream().distinct().forEach(lts::removeStateIfNoIncomingRecursively);
  }

  @Override
  public LTS transform(
      GatewayScope gatewayScope, LTS externalLTS, Graph2LTSTransformer graph2LTSTransformer) {

    if (!new WorkflowTypeDispatcher().isWorkflowASTGateway(gatewayScope.getGraph().getStart())) {
      throw new IllegalStateException("Start node of gatewayScope is not an ASTGateway");
    }
    String splitName = namingStrategy.apply(gatewayScope.getGraph().getStart());
    Optional<String> optMergeName = gatewayScope.getClosingGateway().map(namingStrategy);

    LTS transformedInternalLTS = graph2LTSTransformer.transform(gatewayScope.getGraph());

    LTSWithFinalStates strippedInternalLTS =
        removeInternalSplitAndMerge(transformedInternalLTS, splitName, optMergeName);

    LTSWithFinalStates interleavedLTS =
        getGatewayInterleavingStrategy()
            .interleave(gatewayScope.getGatewayType(), strippedInternalLTS);

    if (optMergeName.isPresent()) {
      var mergeName = optMergeName.get();
      rewireExternalMergeTransitions(externalLTS, mergeName, interleavedLTS);
    }
    // We have to rewire merge transitions first!
    rewireExternalSplitTransitions(externalLTS, splitName, interleavedLTS);

    externalLTS.addTransitionsOf(interleavedLTS);

    removeTransitionsLabeled(splitName, externalLTS);
    optMergeName.ifPresent(s -> removeTransitionsLabeled(s, externalLTS));

    // externalLTS.removeState(interleavedLTS.getStart());

    return externalLTS;
  }
}
