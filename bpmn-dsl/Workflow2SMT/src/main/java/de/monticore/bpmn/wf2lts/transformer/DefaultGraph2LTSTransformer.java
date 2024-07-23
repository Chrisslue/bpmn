package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.DefaultNamingStrategy;
import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.EdgeTo;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import java.util.*;

public class DefaultGraph2LTSTransformer implements Graph2LTSTransformer {

  protected final NamingStrategy<ASTFlowNode> namingStrategy;
  protected final GatewayTransformer gatewayTransformer;
  protected final SubprocessTransformer subprocessTransformer;

  public DefaultGraph2LTSTransformer() {
    this(
        new DefaultNamingStrategy(),
        new DefaultGatewayTransformer(
            new DefaultGatewayInterleaving(), new DefaultNamingStrategy()),
        new DefaultSubprocessTransformer());
  }

  public DefaultGraph2LTSTransformer(
      NamingStrategy<ASTFlowNode> namingStrategy,
      GatewayTransformer gatewayTransformer,
      SubprocessTransformer subprocessTransformer) {
    this.namingStrategy = namingStrategy;
    this.gatewayTransformer = gatewayTransformer;
    this.subprocessTransformer = subprocessTransformer;
  }

  private LTS.Transition transitionFromNodes(
      Map<ASTFlowNode, LTS.State> lookup, ASTFlowNode from, EdgeTo<ASTFlowNode> edgeTo) {
    var source = lookup.get(from);
    var target = lookup.get(edgeTo.getTarget());
    var label = namingStrategy.apply(edgeTo.getTarget());
    var conditions = edgeTo.getConditions();
    return new Transition(source, conditions, label, target);
  }

  protected LTS nodeBasedToTransitionBased(IntermediateGraphWithScopes graph) {
    Map<ASTFlowNode, LTS.State> nodeToState = new HashMap<>();
    var startState = new LTS.State();
    var lts = new LTS(startState);
    // Create a corresponding state for every node.
    graph.getEdges().keySet().forEach(flowNode -> nodeToState.put(flowNode, new LTS.State()));
    // Add any target that is not in the key set.
    graph.getEdges().values().stream()
        .flatMap(Collection::stream)
        .map(EdgeTo::getTarget)
        .forEach(flowNode -> nodeToState.putIfAbsent(flowNode, new LTS.State()));
    // Add the first transition manually as graph.getStart() has no incoming edges
    lts.addTransition(
        new Transition(
            startState,
            Collections.emptyList(),
            namingStrategy.apply(graph.getStart()),
            nodeToState.get(graph.getStart())));
    graph.getEdges().keySet().stream()
        .filter(
            node ->
                node != graph.getStart()
                    && graph.getEdges().values().stream()
                        .noneMatch(
                            transitionList ->
                                transitionList.stream()
                                    .anyMatch(transition -> transition.getTarget() == node)))
        .forEach(
            node ->
                lts.addTransition(
                    new Transition(
                        new State(),
                        Collections.emptyList(),
                        namingStrategy.apply(node),
                        nodeToState.get(node))));

    graph
        .getEdges()
        .forEach(
            (node, edgeList) ->
                edgeList.forEach(
                    edgeTo -> lts.addTransition(transitionFromNodes(nodeToState, node, edgeTo))));
    return lts;
  }

  protected LTS transformMetaElements(LTS externalLTS, IntermediateGraphWithScopes graph) {
    graph
        .getGatewayScopes()
        .forEach(gatewayScope -> gatewayTransformer.transform(gatewayScope, externalLTS, this));
    graph
        .getSubProcessScopes()
        .forEach(
            subProcessScope ->
                subprocessTransformer.transform(
                    subProcessScope, externalLTS, namingStrategy, this));
    return externalLTS;
  }

  protected LTS removeEpsilonTransitions(LTS lts) {
    List<Transition> epsilonTransitions = new ArrayList<>(lts.getTransitionsForLabel(""));
    while (!epsilonTransitions.isEmpty()) {
      Transition transition = epsilonTransitions.get(0);
      List<Transition> successors = lts.getOutgoings(transition.getTarget());
      successors.remove(transition);
      successors.forEach(
          successor -> lts.addTransition(successor.changedSource(transition.getSource())));
      lts.removeTransition(transition);
      epsilonTransitions = new ArrayList<>(lts.getTransitionsForLabel(""));
    }
    return lts;
  }

  /**
   * Push name of nodes to incoming edges. 1. Obtain name of node with namingStrategy. 2. Create lts
   * with states instead of nodes. 3. Transform source, edgeTo to transition with label=name of
   * target 4. Transform meta elements with their transformer strategies.
   */
  @Override
  public LTS transform(IntermediateGraphWithScopes graph) {
    LTS transitionBased = nodeBasedToTransitionBased(graph);
    return transformMetaElements(transitionBased, graph);
  }

  @Override
  public LTS transformAndReduce(IntermediateGraphWithScopes graph) {
    // todo: merge redundant states
    return removeEpsilonTransitions(transform(graph));
  }
}
