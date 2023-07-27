package de.monticore.bpmn.wf2lts.datastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IntermediateGraph<S, E extends EdgeTo<S>> {

  protected Map<S, List<E>> edges;

  protected S start;

  public IntermediateGraph(S startNode) {
    this(startNode, new HashMap<>());
  }

  public IntermediateGraph(S startNode, Map<S, List<E>> edges) {
    this.edges = edges;
    this.start = startNode;
  }

  protected List<S> predecessorNodes(S node) {
    List<S> predecessor = new ArrayList<>();
    getEdges()
        .forEach(
            (key, edgeList) -> {
              if (edgeList.stream().anyMatch(edge -> edge.getTarget().equals(node))) {
                predecessor.add(key);
              }
            });
    return predecessor;
  }

  /**
   * @param node for which all predecessors and their edges should be collected.
   * @return All predecessors of node with only their edges to node.
   */
  protected Map<S, List<E>> predecessors(S node) {
    Map<S, List<E>> predecessorSubSet = new HashMap<>();
    getEdges()
        .forEach(
            (key, edgeList) -> {
              var relevantEdges =
                  edgeList.stream()
                      .filter(edge -> edge.getTarget().equals(node))
                      .collect(Collectors.toList());
              predecessorSubSet.put(key, relevantEdges);
            });
    return predecessorSubSet;
  }

  protected List<S> getSources(E edge) {
    return getEdges().entrySet().stream()
        .filter(entry -> entry.getValue().contains(edge))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  public S getStart() {
    return start;
  }

  protected Map<S, List<E>> getEdges() {
    return edges;
  }
}
