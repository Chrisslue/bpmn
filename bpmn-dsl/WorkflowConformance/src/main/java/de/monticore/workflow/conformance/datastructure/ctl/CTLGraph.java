package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class CTLGraph {
  public final DefaultDirectedGraph<CTLNode, CTLEdge> graph;
  private Set<CTLNode> ctlNodes;

  public CTLGraph() {
    this.graph = new DefaultDirectedGraph<>(CTLEdge.class);
    ctlNodes = new HashSet<>();
  }

  public CTLNode addNode(Set<IDWfNode> labels, Set<IDWfNode> activeNodes) {
    if (getNode(labels).isPresent()) {
      return getNode(labels).get();
    }
    var res = new CTLNode(labels, activeNodes);
    graph.addVertex(res);
    ctlNodes.add(res);
    return res;
  }

  public void addEdge(CTLNode from, CTLNode to) {
    assert isNodePresent(from);
    assert isNodePresent(to);
    graph.addEdge(from, to);
  }

  private boolean isNodePresent(CTLNode node) {
    return this.ctlNodes.contains(node);
  }

  public boolean checkPredicate(Predicate<Set<IDWfNode>> predicate) {
    for (CTLNode vertex : graph.vertexSet()) {
      if (graph.outDegreeOf(vertex) == 0) {
        if (!predicate.test(vertex.getLabels())) {
          return false;
        }
      }
    }

    return true;
  }

  public Optional<CTLNode> getNode(Set<IDWfNode> labels) {
    return ctlNodes.stream()
        .filter(
            node -> node.getLabels().containsAll(labels) && labels.containsAll(node.getLabels()))
        .findAny();
  }
}
