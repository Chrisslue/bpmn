package de.monticore.wf2ltl.transformer;

import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.wf2ltl.NamingStrategy;
import de.monticore.wf2ltl.datastructure.EdgeTo;
import de.monticore.wf2ltl.datastructure.IntermediateGraphWithScopes;
import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.datastructure.LTS.State;
import de.monticore.wf2ltl.datastructure.LTS.Transition;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultGraph2LTSTransformer implements Graph2LTSTransformer {

  private final NamingStrategy namingStrategy;
  private final GatewayTransformer gatewayTransformer;
  private final SubprocessTransformer subprocessTransformer;

  public DefaultGraph2LTSTransformer(
      NamingStrategy namingStrategy,
      GatewayTransformer gatewayTransformer,
      SubprocessTransformer subprocessTransformer
  ) {
    this.namingStrategy = namingStrategy;
    this.gatewayTransformer = gatewayTransformer;
    this.subprocessTransformer = subprocessTransformer;
  }

  private LTS.Transition transitionFromNodes(Map<ASTFlowNode, LTS.State> lookup, ASTFlowNode from,
      EdgeTo<ASTFlowNode> edgeTo) {
    var source = lookup.get(from);
    var target = lookup.get(edgeTo.getTarget());
    var label = namingStrategy.apply(edgeTo.getTarget());
    var conditions = edgeTo.getConditions();
    return new Transition(source, conditions, label, target);
  }

  private LTS nodeBasedToTransitionBased(IntermediateGraphWithScopes graph) {
    Map<ASTFlowNode, LTS.State> nodeToState = new HashMap<>();
    var startState = new LTS.State();
    var lts = new LTS(startState);
    // Create a corresponding state for every node.
    graph.getEdges().keySet().forEach(flowNode -> nodeToState.put(flowNode, new LTS.State()));
    // Add any target that is not in the key set.
    graph.getEdges().values()
        .stream()
        .flatMap(Collection::stream)
        .map(EdgeTo::getTarget)
        .forEach(flowNode -> nodeToState.putIfAbsent(flowNode, new LTS.State()));
    //Add the first transition manually as graph.getStart() has no incoming edges
    lts.addTransition(
        new Transition(
            startState,
            Collections.emptyList(),
            namingStrategy.apply(graph.getStart()),
            nodeToState.get(graph.getStart())
        )
    );
    graph.getEdges().keySet().stream()
        .filter(node -> node != graph.getStart() && graph.getEdges().values().stream().noneMatch(
            transitionList -> transitionList.stream().anyMatch(transition -> transition.getTarget() == node)))
        .forEach(node ->
            lts.addTransition(
                new Transition(
                    new State(),
                    Collections.emptyList(),
                    namingStrategy.apply(node),
                    nodeToState.get(node)
                )
            )
        );

    graph.getEdges().forEach((node, edgeList) ->
        edgeList.forEach(edgeTo ->
            lts.addTransition(transitionFromNodes(nodeToState, node, edgeTo))
        )
    );
    return lts;
  }

  private LTS transformMetaElements(LTS externalLTS, IntermediateGraphWithScopes graph) {
    graph.getGatewayScopes().forEach(gatewayScope ->
        gatewayTransformer.transform(gatewayScope, externalLTS, namingStrategy, this)
    );
    graph.getSubProcessScopes().forEach(subProcessScope ->
        subprocessTransformer.transform(subProcessScope, externalLTS, namingStrategy, this));
    return externalLTS;
  }

  /**
   * Push name of nodes to incoming edges. 1. Obtain name of node with namingStrategy. 2. Create lts with states instead
   * of nodes. 3. Transform source, edgeTo to transition with label=name of target 4. Transform meta elements with their
   * transformer strategies.
   */
  @Override
  public LTS transform(IntermediateGraphWithScopes graph) {
    LTS transitionBased = nodeBasedToTransitionBased(graph);
    return transformMetaElements(transitionBased, graph);
  }
}
