package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class CTLGraph {
  public final DefaultDirectedGraph<CTLNode, DefaultEdge> graph;
  private final Set<CTLNode> nodes;

  public CTLGraph() {
    this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    nodes = new HashSet<>();
  }

  public void addNode(CTLNode node) {
    if (!isNodePresent(node)) {
      nodes.add(node);
      graph.addVertex(node);
    }
  }

  public void addEdge(CTLNode from, CTLNode to) {
    assert isNodePresent(from);
    assert isNodePresent(to);
    graph.addEdge(from, to);
  }

  private boolean isNodePresent(CTLNode node) {
    return this.nodes.contains(node);
  }

  public boolean checkPredicate(Predicate<Set<IdWfNode>> predicate) {
    for (CTLNode vertex : graph.vertexSet()) {
      if (graph.outDegreeOf(vertex) == 0) {
        if (!predicate.test(vertex.getLabels())) {
          return false;
        }
        ;
      }
    }

    return true;
  }
}
