package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class CTLNode {
  private final Set<IDWfNode> labels;

  private final Set<IDWfNode> activeNode;

  protected CTLNode(Set<IDWfNode> labels, Set<IDWfNode> activeNodes) {
    this.labels = Collections.unmodifiableSet(labels);
    this.activeNode = new HashSet<>(activeNodes);
  }

  public Set<IDWfNode> getActiveNodes() {
    return activeNode;
  }

  public  void removeActiveNodes(Set<IDWfNode> nodes) {
    activeNode.removeAll(nodes);
  }

  public  void addActiveNodes(Set<IDWfNode> nodes) {
    activeNode.addAll(nodes);
  }

  public Set<IDWfNode> getLabels() {
    return labels;
  }

  @Override
  public String toString() {
    return "[" + new TreeSet<>(labels.stream().map(IDWfNode::getLabel).collect(Collectors.toSet())) + "|" + new TreeSet<>(activeNode.stream().map(IDWfNode::getLabel).collect(Collectors.toSet())) + "]";
  }
}
