package de.monticore.workflow.conformance.datastructure;

import java.util.ArrayList;
import java.util.List;

public class BPMNNode {
  private final BPMNNodeType type;
  private final List<BPMNNode> successors;
  private String name;
  private final List<BPMNNode> children = new ArrayList<>();

  public BPMNNode(String name, BPMNNodeType type) {
    this.type = type;
    successors = new ArrayList<>();
    this.name = name;
  }

  public static BPMNNode mkSequence(List<BPMNNode> nodeList) {
    return new BPMNNode(null, BPMNNodeType.SEQUENCE, nodeList);
  }

  public BPMNNode(String name, BPMNNodeType type, List<BPMNNode> children) {
    this(name, type);
    this.children.addAll(children);
  }

  public BPMNNodeType getType() {
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
