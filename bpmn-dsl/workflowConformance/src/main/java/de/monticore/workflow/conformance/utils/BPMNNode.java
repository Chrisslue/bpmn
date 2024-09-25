package de.monticore.workflow.conformance.utils;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import java.util.ArrayList;
import java.util.List;

public class BPMNNode {
  private final NodeType type;
  private final List<BPMNNode> successors;
  private String name;
  private final List<BPMNNode> children = new ArrayList<>();

  public BPMNNode(String name, NodeType type) {
    this.type = type;
    successors = new ArrayList<>();
    this.name = name;
  }

  public BPMNNode(String name, NodeType type, List<BPMNNode> children) {
    this(name, type);
    this.children.addAll(children);
  }

  public NodeType getType() {
    return type;
  }

  public List<BPMNNode> getSuccessors() {
    return successors;
  }

  public void addSuccessors(List<BPMNNode> nodes) {
    successors.addAll(nodes);
  }

  public void addSuccessor(BPMNNode node) {
    successors.add(node);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + ":" + type.toString();
  }
}
