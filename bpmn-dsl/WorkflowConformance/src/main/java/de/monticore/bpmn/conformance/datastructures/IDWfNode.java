package de.monticore.bpmn.conformance.datastructures;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.NodeType;
import de.monticore.umlstereotype._ast.ASTStereotype;

import java.util.*;

public class IDWfNode implements WfNode {
  private Set<IDWfNode> predecessors = Collections.unmodifiableSet(new HashSet<>());
  private Set<IDWfNode> successors = Collections.unmodifiableSet(new HashSet<>());
  private final boolean isStart;

  private final boolean isEnd;
  private final String label;
  private final NodeType nodeType;
  private  final ASTStereotype stereotype;

  @Override
  public Set<IDWfNode> getPredecessors() {
    return predecessors;
  }

  @Override
  public Set<IDWfNode> getSuccessors() {
    return successors;
  }

  public IDWfNode(String label, NodeType nodeType,ASTStereotype stereotype, boolean isStart, boolean isEnd) {
    this.isStart = isStart;
    this.isEnd = isEnd;
    this.label = label;
    this.nodeType = nodeType;
      this.stereotype = stereotype;
  }

  @Override
  public NodeType getNodeType() {
    return nodeType;
  }

  @Override
  public boolean isStart() {
    return isStart;
  }

  @Override
  public boolean isEnd() {
    return isEnd;
  }

  @Override
  public Optional<ASTStereotype> getStereotype() {
    return Optional.ofNullable(stereotype);
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

  public void addAllPredecessors(Set<IDWfNode> predecessor) {
    this.predecessors = Collections.unmodifiableSet(predecessor);
  }

  public void addAllSuccessors(Set<IDWfNode> successor) {
    this.successors = Collections.unmodifiableSet(successor);
  }
}
