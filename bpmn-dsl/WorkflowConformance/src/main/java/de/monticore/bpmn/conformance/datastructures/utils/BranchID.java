package de.monticore.bpmn.conformance.datastructures.utils;

import static de.monticore.bpmn.conformance.datastructures.utils.NodeType.*;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*****
 * This class stores information for a given branch during the traversal of the BPMN.
 *
 * @author valdes-voufo
 *
 * It stores:
 * - id: the ID of the branch.
 * - nodeList: the list of visited nodes in the visiting order.
 * - checkCompleted: true when the check is completed on the branch, and it will not be expanded anymore.
 * - aborted: true when the branch is abandoned, for example, after an XOR split.
 * - waitingAndMerge: a list of nodes of type AND_MERGE that are waiting to be merged.
 *
 */

public class BranchID {
  private final int id;
  private final List<WfNode> nodeList;

  private static int counter = 0;
  private boolean checkCompleted = false;
  private boolean checkAborted = false;
  private Set<WfNode> waitingAndMerge = new HashSet<>();

  CheckResult loweBoundResult;
  CheckResult upperBoundResult;
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

  public void addNode(WfNode node) {
    if (node.getNodeType().equals(AND_MERGE) && waitingAndMerge.contains(node)) {
      return;
    }
    if (nodeList.contains(node)) { // todo handle it properly
      checkCompleted = true;
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

  public void completeCheck() {
    this.checkCompleted = true;
  }

  public boolean isCheckCompleted() {
    return checkCompleted;
  }

  public void abortCheck() {
    this.checkAborted = true;
  }

  public boolean isCheckAborted() {
    return checkAborted;
  }



  public void setUpperBoundResult(CheckResult upperBoundResult) {
    this.upperBoundResult = upperBoundResult;
  }

  public void setLoweBoundResult(CheckResult loweBoundResult) {
    this.loweBoundResult = loweBoundResult;
  }


  public CheckResult getUpperBoundResult() {
    return upperBoundResult;
  }

  public CheckResult getLoweBoundResult() {
    return loweBoundResult;
  }
}
