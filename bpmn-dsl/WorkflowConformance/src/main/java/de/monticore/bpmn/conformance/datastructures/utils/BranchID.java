package de.monticore.bpmn.conformance.datastructures.utils;

import static de.monticore.bpmn.conformance.datastructures.utils.NodeType.*;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.List;

public class BranchID {
  private final int id;
  private final List<WfNode> nodeList;
  private boolean loopDetected = false;
  private boolean inParallel = false;
  private static int counter = 0;
  private boolean aborted = false;

  public BranchID(List<WfNode> nodeList) {
    this.id = counter++;
    this.nodeList = nodeList;
  } // todo handle when many parallel level

  public boolean isLoopDetected() {
    return loopDetected;
  }

  public int getId() {
    return id;
  }

  public void addNode(WfNode node) {
    if (nodeList.contains(node)
        && !node.getNodeType().equals(AND_MERGE)) { // todo handle it properly
      loopDetected = true;
    }

    this.nodeList.add(node);
  }

  public boolean isInParallel() {
    return inParallel;
  }

  public void setInParallel(boolean inParallel) {
    this.inParallel = inParallel;
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

  public void setAborted() {
    this.aborted = true;
  }

  public boolean isAborted() {
    return aborted;
  }
}
