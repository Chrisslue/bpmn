package de.monticore.workflow.conformance.utils;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.List;

public class BranchID {
  private final int id;
  private final List<WfNode> nodeList;
  private boolean loopDetected = false;

  public BranchID(List<WfNode> nodeList, int id) {
    this.id = id;
    this.nodeList = nodeList;
  }

  public boolean isLoopDetected() {
    return loopDetected;
  }

  public int getId() {
    return id;
  }

  public void addNode(WfNode node) {
    if (nodeList.contains(node)) {
      loopDetected = true;
    }
    this.nodeList.add(node);
  }

  public List<WfNode> getNodeList() {
    return nodeList;
  }

  public boolean isEmpty() {
    return nodeList.isEmpty();
  }

  @Override
  public String toString() {
    return "id:" + id + " " + nodeList.toString();
  }
}
