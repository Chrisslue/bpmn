package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CTLNode {
  private final Set<IDWfNode> labels;

  private IDWfNode activeNode;

  public CTLNode(Set<IDWfNode> labels, IDWfNode activeNodes) {
    this.labels = Collections.unmodifiableSet(labels);
    this.activeNode = activeNodes;
    ctlNodes.add(this);
  }

  public IDWfNode getActiveNodes() {
    return activeNode;
  }

  private static final Set<CTLNode> ctlNodes = new HashSet<>();

  public Set<IDWfNode> getLabels() {
    return labels;
  }

  public static CTLNode mkNode(Set<IDWfNode> labels, IDWfNode activeNodes) {

    if (getNode(labels).isPresent()) {
      CTLNode res = getNode(labels).get();
      res.activeNode = activeNodes;
      return res;
    }
    return getNode(labels).orElseGet(() -> new CTLNode(labels, activeNodes));
  }

  public static Optional<CTLNode> getNode(Set<IDWfNode> labels) {
    return ctlNodes.stream()
        .filter(node -> node.labels.containsAll(labels) && labels.containsAll(node.labels))
        .findAny();
  }

  @Override
  public String toString() {
    return "[" + labels.toString() + "|" + activeNode.toString() + "]";
  }
}
