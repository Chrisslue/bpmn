package de.monticore.wf2lts.transformer;

import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.bpmn.workflow._util.WorkflowTypeDispatcher;
import de.monticore.wf2lts.NamingStrategy;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import de.monticore.wf2lts.scopes.GatewayScope;
import java.util.List;
import java.util.Optional;

public class DefaultGatewayTransformer implements GatewayTransformer {

  private final GatewayInterleavingStrategy interleavingStrategy;

  public DefaultGatewayTransformer(GatewayInterleavingStrategy interleavingStrategy) {
    this.interleavingStrategy = interleavingStrategy;
  }

  @Override
  public GatewayInterleavingStrategy getGatewayInterleavingStrategy() {
    return interleavingStrategy;
  }

  /**
   * Add all outgoings of internal start to all sources of external transitions labeled with
   * splitName. Assuming splitName is not part of the internal lts anymore.
   *
   * @param externalLTS The lts surrounding the GatewayScope.
   * @param splitName The name of the splitting gateway.
   * @param internalLTS The lts representing all transitions encapsulated by the GatewayScope.
   */
  private static void transformSplit(LTS externalLTS, String splitName, LTS internalLTS) {
    List<Transition> oldTransitions = externalLTS.getTransitionsForLabel(splitName);
    if (internalLTS.isLabelPresent(splitName)) {
      throw new IllegalStateException("Internal LTS should not contain transitions with splitName");
    }
    List<Transition> internalStartTransitions = internalLTS.getOutgoings(internalLTS.getStart());
    for (LTS.Transition oldTransition : oldTransitions) {
      for (LTS.Transition internalStartTransition : internalStartTransitions) {
        externalLTS.addTransition(
            internalStartTransition
                .changedSource(oldTransition.getSource())
                .withAddedConditions(oldTransition.getConditions()));
      }
    }
  }

  /**
   * Replace all internal transitions labeled with mergeName with all external successor transitions
   * of the gateway. We don't have to consider terminal states ending in an end even, those paths
   * just end.
   *
   * @param externalLTS The lts surrounding the GatewayScope.
   * @param mergeName The name of the merging gateway. Appearing both in the external and internal
   *     lts.
   * @param internalLTS The lts representing all transitions encapsulated by the GatewayScope
   */
  private static void transformMerge(
      LTS externalLTS, String mergeName, LTS internalLTS, List<Transition> internalMerging) {
    List<Transition> externalMerging = externalLTS.getTransitionsForLabel(mergeName);
    if (internalMerging.isEmpty() || externalMerging.isEmpty()) {
      throw new IllegalStateException(
          "No merging transitions in lts. Expected to find"
              + mergeName
              + " .In lts: "
              + (internalMerging.isEmpty() ? internalLTS : externalLTS));
    }
    for (var internalMergingTransition : internalMerging) {
      for (var externalMergeTransition : externalMerging) {
        List<Transition> externalSuccessors =
            externalLTS.getOutgoings(externalMergeTransition.getTarget());
        for (LTS.Transition externalSuccessor : externalSuccessors) {
          internalLTS.addTransition(
              externalSuccessor
                  .changedSource(internalMergingTransition.getSource())
                  .withAddedConditions(internalMergingTransition.getConditions()));
        }
      }
    }
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
    cleanUp(splitName, internalLTS);
  }

  private List<Transition> removeInternalMergeTransition(LTS internalLTS, String mergeName) {
    var mergeTransitions = internalLTS.getTransitionsForLabel(mergeName);
    if (mergeTransitions.stream()
        .anyMatch(transition -> !internalLTS.getOutgoings(transition.getTarget()).isEmpty())) {
      throw new IllegalStateException("Merge transition target had outgoing transitions");
    }
    mergeTransitions.forEach(internalLTS::removeTransition);
    return mergeTransitions;
  }

  public static void cleanUp(String name, LTS lts) {
    List<Transition> transitionsToBeRemoved = lts.getTransitionsForLabel(name);
    transitionsToBeRemoved.forEach(lts::removeTransition);
    lts.removeTargetIfNoIncomings(transitionsToBeRemoved);
  }

  @Override
  public void transform(
      GatewayScope gatewayScope,
      LTS externalLTS,
      NamingStrategy<IFlowNode> namingStrategy,
      Graph2LTSTransformer subprocessTransformer) {

    if (!new WorkflowTypeDispatcher().isASTGateway(gatewayScope.getGraph().getStart())) {
      throw new IllegalStateException("Start node of gatewayScope is not an ASTGateway");
    }
    String splitName = namingStrategy.apply(gatewayScope.getGraph().getStart());
    Optional<String> optMergeName = gatewayScope.getClosingGateway().map(namingStrategy);

    LTS transformedInternalLTS = subprocessTransformer.transform(gatewayScope.getGraph());
    removeInternalSplitTransitions(transformedInternalLTS, splitName);
    LTS finalTransformedInternalLTS = transformedInternalLTS;
    Optional<List<Transition>> optMergeTransitions =
        optMergeName.map(
            mergeName -> removeInternalMergeTransition(finalTransformedInternalLTS, mergeName));

    transformedInternalLTS =
        getGatewayInterleavingStrategy()
            .interleave(gatewayScope.getGatewayType(), transformedInternalLTS);

    transformSplit(externalLTS, splitName, transformedInternalLTS);

    if (optMergeName.isPresent()) {
      var mergeName = optMergeName.get();
      transformMerge(
          externalLTS, mergeName, transformedInternalLTS, optMergeTransitions.orElseThrow());
      cleanUp(mergeName, transformedInternalLTS);
      cleanUp(mergeName, externalLTS);
    }
    cleanUp(splitName, externalLTS);
    // cleanUp for splitName and internal lts is done in removeInternalSplitTransitions.

    externalLTS.addTransitionsOf(transformedInternalLTS);
  }
}
