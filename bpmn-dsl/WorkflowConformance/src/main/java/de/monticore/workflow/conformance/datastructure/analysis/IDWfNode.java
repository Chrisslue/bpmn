package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.*;

public class IDWfNode implements WfNode {
  private Set<IDWfNode> predecessors = Collections.unmodifiableSet(new HashSet<>());
  private Set<IDWfNode> successors = Collections.unmodifiableSet(new HashSet<>());

  private final String label;
  private final NodeType nodeType;

  public Set<IDWfNode> getPredecessors() {
    return predecessors;
  }

  public Set<IDWfNode> getSuccessors() {
    return successors;
  }

  protected IDWfNode(String label, NodeType nodeType) {
    this.label = label;
    this.nodeType = nodeType;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public void addPredecessors(Set<IDWfNode> predecessor) {
    this.predecessors = Collections.unmodifiableSet(predecessor);
  }

  public void addSuccessors(Set<IDWfNode> successor) {
    this.successors = Collections.unmodifiableSet(successor);
  }

  @Override
  public String getLabel() {
    return label;
  }

  public boolean isGateway() {
    return Set.of(
            NodeType.AND_SPLIT,
            NodeType.OR_SPLIT,
            NodeType.XOR_SPLIT,
            NodeType.OR_MERGE,
            NodeType.AND_MERGE,
            NodeType.XOR_MERGE)
        .contains(nodeType);
  }

  @Override
  public String toString() {
    return label;
  }
}
