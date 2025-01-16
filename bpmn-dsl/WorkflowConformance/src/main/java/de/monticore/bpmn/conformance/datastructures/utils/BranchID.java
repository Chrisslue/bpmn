package de.monticore.bpmn.conformance.datastructures.utils;

import static de.monticore.bpmn.conformance.datastructures.utils.NodeType.*;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BranchID {
  private final int id;
  private final List<WfNode> nodeList;
  private boolean loopDetected = false;
  private boolean inParallel = false;
  private static int counter = 0;
  private boolean aborted = false;
  private boolean ignore = false;
  private Set<WfNode> waitingAndMerge = new HashSet<>();

  public void merge(WfNode andMerge) {
    waitingAndMerge.remove(andMerge);
  }

  public void wait(WfNode andMerge) {
    assert andMerge.getNodeType().equals(AND_MERGE);
    waitingAndMerge.add(andMerge);
  }

  public BranchID(List<WfNode> nodeList) {
    this.id = counter++;
    this.nodeList = nodeList;
  } // todo handle when many parallel level

  public boolean isLoopDetected() {
    return loopDetected;
  }

  public void addNode(WfNode node) {
    if (node.getNodeType().equals(AND_MERGE) && waitingAndMerge.contains(node)) {
      return;
    }
    if (nodeList.contains(node)) { // todo handle it properly
      loopDetected = true;
      aborted = true;
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

  public void setIgnore() {
    this.ignore = true;
  }

  public boolean isIgnore() {
    return ignore;
  }
}
