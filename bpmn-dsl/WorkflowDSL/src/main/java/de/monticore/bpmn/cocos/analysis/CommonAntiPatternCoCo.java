package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.analysis.graph.WorkflowGraphConverter;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._ast.ASTGatewayType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.lca.NaiveLCAFinder;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

abstract class CommonAntiPatternCoCo extends ProcessGraphCoCo {

  AllDirectedPaths<ASTFlowElement, EndpointPair<ASTFlowElement>> pathFinder;
  NaiveLCAFinder<ASTFlowElement, EndpointPair<ASTFlowElement>> lcaFinder;

  @Override
  public void check(final ASTWFProcess process) {
    ImmutableGraph<ASTFlowElement> graph = new WorkflowGraphConverter(process).convert().getGraph();

    this.processGraph = new ImmutableGraphAdapter<>(graph);
    this.process = process;

    pathFinder = new AllDirectedPaths<>(processGraph);
    lcaFinder = new NaiveLCAFinder<>(processGraph);

    check(new ImmutableGraphAdapter<>(graph), process);
  }

  /**
   * Returns all inner nodes of the path whose predecessors that are not on the path cannot be
   * reached from any node of the path.
   *
   * @param path
   * @return
   */
  List<ASTFlowElement> getPathEntries(final GraphPath<ASTFlowElement, EndpointPair<ASTFlowElement>> path) {
    List<ASTFlowElement> innerNodes = getPathInnerNodes(path);

    return innerNodes.stream()
        .filter(
            node ->
                node.getPredecessors().stream()
                    .filter(predecessor -> !path.getVertexList().contains(predecessor))
                    .anyMatch(
                        predecessor ->
                            pathFinder
                                .getAllPaths(
                                    Sets.newHashSet(innerNodes),
                                    Sets.newHashSet(predecessor),
                                    true,
                                    null)
                                .isEmpty()))
        .collect(Collectors.toList());
  }

  /**
   * Returns all inner parallel gateways of the path whose successors that are not on the path
   * cannot reach any nodes of the path.
   *
   * @param path
   * @return
   */
  List<ASTFlowElement> getPathExits(final GraphPath<ASTFlowElement, EndpointPair<ASTFlowElement>> path) {
    List<ASTFlowElement> innerNodes = getPathInnerNodes(path);

    return innerNodes.stream()
        .flatMap(this::isNonParallelGateway)
        .filter(
            node ->
                node.getPredecessors().stream()
                    .filter(predecessor -> !path.getVertexList().contains(predecessor))
                    .anyMatch(
                        predecessor ->
                            pathFinder
                                .getAllPaths(
                                    Sets.newHashSet(innerNodes),
                                    Sets.newHashSet(predecessor),
                                    true,
                                    null)
                                .isEmpty()))
        .collect(Collectors.toList());
  }

  private List<ASTFlowElement> getPathInnerNodes(
      final GraphPath<ASTFlowElement, EndpointPair<ASTFlowElement>> path) {
    return path.getVertexList().stream()
        .filter(node -> node != path.getStartVertex() && node != path.getEndVertex())
        .collect(Collectors.toList());
  }

  private Stream<ASTWFGateway> isNonParallelGateway(final ASTFlowElement flowNode) {
    return Stream.of(flowNode)
        .flatMap(WorkflowFilters::isGateway)
        .filter(ASTWFGateway::isDiverging)
        .filter(
            split -> {
              ASTGatewayType type = split.getType();
              return type.isExclusive()
                  || type.isExclusiveEventBased()
                  || type.isInclusive()
                  || type.isComplex();
            });
  }
}
