package de.monticore.workflow.conformance.datastructure.ctl;

import java.util.HashSet;
import java.util.Set;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class CTLGraph {
  public final DefaultDirectedGraph<TokenController, DefaultEdge> graph;
  private final Set<TokenController> nodes;

  public CTLGraph() {
    this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    nodes = new HashSet<>();
  }

  public void addNode(TokenController node) {
    if (!isNodePresent(node)) {
      nodes.add(node);
      graph.addVertex(node);
    }
  }

  public void addEdge(TokenController from, TokenController to) {
    assert isNodePresent(from);
    assert isNodePresent(from);
    graph.addEdge(from, to);
  }

  private boolean isNodePresent(TokenController node) {
    return this.nodes.contains(node);
  }
}
