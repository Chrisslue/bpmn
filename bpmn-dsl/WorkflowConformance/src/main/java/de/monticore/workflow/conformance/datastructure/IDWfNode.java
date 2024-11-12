package de.monticore.workflow.conformance.datastructure;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.utils.NodeType;
import java.util.*;

public class IDWfNode implements WfNode {
  private Set<IDWfNode> predecessors = Collections.unmodifiableSet(new HashSet<>());
  private Set<IDWfNode> successors = Collections.unmodifiableSet(new HashSet<>());

  private final String label;
  private final NodeType nodeType;

  @Override
  public Set<IDWfNode> getPredecessors() {
    return predecessors;
  }

  @Override
  public Set<IDWfNode> getSuccessors() {
    return successors;
  }

  protected IDWfNode(String label, NodeType nodeType) {
    this.label = label;
    this.nodeType = nodeType;
  }

  @Override
  public NodeType getNodeType() {
    return nodeType;
  }

  @Override
  public String getLabel() {
    return label;
  }

  private Set<IDWfNode> getSuccessors(int depth) {
    if (depth == 1) {
      return successors;
    }

    Set<IDWfNode> res = new HashSet<>(successors);

    for (IDWfNode suc : successors) {
      res.addAll(suc.getSuccessors(depth - 1));
    }
    return res;
  }

  private Set<IDWfNode> getPredecessors(int depth) {
    if (depth == 1) {
      return predecessors;
    }

    Set<IDWfNode> res = new HashSet<>(predecessors);

    for (IDWfNode suc : predecessors) {
      res.addAll(suc.getPredecessors(depth - 1));
    }
    return res;
  }

  @Override
  public Set<IDWfNode> getSuccessorsOfDepth(int depth) {
    if (depth == 0) {
      return new HashSet<>();
    }

    Set<IDWfNode> res = getSuccessors(depth);
    res.removeAll(getSuccessors(depth - 1));
    return res;
  }

  @Override
  public Set<? extends WfNode> getPredecessorsOfDepth(int depth) {
    if (depth == 0) {
      return new HashSet<>();
    }

    Set<IDWfNode> res = getPredecessors(depth);
    res.removeAll(getPredecessors(depth - 1));
    return res;
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

  public void addAllPredecessors(Set<IDWfNode> predecessor) {
    this.predecessors = Collections.unmodifiableSet(predecessor);
  }

  public void addAllSuccessors(Set<IDWfNode> successor) {
    this.successors = Collections.unmodifiableSet(successor);
  }
}
