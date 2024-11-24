package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.utils.BranchID;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class ConfWfTraverser {

  private int counter = 0;

  public void traverseForward(WfNodeVisitor wfVisitor, BranchID branchId, WfNode node) {
    if (branchId == null) {
      branchId = new BranchID(new ArrayList<>(), counter++);
    }
    Log.trace(String.format("Traversing node %s forward", node), "");

    switch (node.getNodeType()) {
      case EVENT:
      case TASK:
        boolean abort = !wfVisitor.accept(node, branchId);
        if (abort) {
          return;
        }
        assert node.getSuccessors().size() <= 1;
        BranchID finalBranchId = branchId;
        node.getSuccessors().forEach(suc -> this.traverseForward(wfVisitor, finalBranchId, suc));
        break;

      case AND_SPLIT:
      case AND_MERGE:
      case XOR_MERGE:
        BranchID finalBranchId1 = branchId;
        node.getSuccessors().forEach(suc -> this.traverseForward(wfVisitor, finalBranchId1, suc));
        break;

      case XOR_SPLIT:
        for (WfNode suc : node.getSuccessors()) {
          var newNodeLists = new ArrayList<>(branchId.getNodeList());
          this.traverseForward(wfVisitor, new BranchID(newNodeLists, counter++), suc);
        }
        break;

      case OR_SPLIT:
      case OR_MERGE:
        assert false; // TODO implement me
    }

    Log.trace(String.format("Finish traversing %s forward", node.getLabel()), "");
  }
}
