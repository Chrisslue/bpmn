package de.monticore.bpmn.conformance.conformance.ctlConformance;

import static de.monticore.bpmn.conformance.datastructures.utils.NodeType.OR_MERGE;

import com.google.common.collect.Sets;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.interf.WfNodeVisitor;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class ConfWfTraverser {

  private final String errorMsg = "%s traversing of node wit type %s not implemented yet";

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
        //todo  wait the other branches

      case XOR_MERGE:
      case OR_MERGE:
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
        Set<? extends Set<? extends WfNode>> sucessorsPowerSet =
            Sets.powerSet(node.getSuccessors());

        for (var successorSubset : sucessorsPowerSet) {
          var newNodeLists = new ArrayList<>(branchId.getNodeList());
          BranchID branchID = new BranchID(newNodeLists, counter++);
          successorSubset.forEach(suc -> this.traverseForward(wfVisitor, branchID, suc));
        }
        break;
    }

    Log.trace(String.format("Finish traversing %s forward", node.getLabel()), "");
  }

  public void traverseBackward(WfNodeVisitor wfVisitor, BranchID branchId, WfNode node) {
    if (branchId == null) {
      branchId = new BranchID(new ArrayList<>(), counter++);
    }
    Log.trace(String.format("Traversing node %s backward", node), "");

    switch (node.getNodeType()) {
      case EVENT:
      case TASK:
        boolean abort = !wfVisitor.accept(node, branchId);
        if (abort) {
          return;
        }
        assert node.getPredecessors().size() <= 1;
        BranchID finalBranchId = branchId;
        node.getPredecessors().forEach(suc -> this.traverseBackward(wfVisitor, finalBranchId, suc));
        break;

      case AND_MERGE:
      case AND_SPLIT:
      case XOR_SPLIT:
      case XOR_MERGE:
      case OR_SPLIT:
        BranchID finalBranchId1 = branchId;
        node.getPredecessors()
            .forEach(suc -> this.traverseBackward(wfVisitor, finalBranchId1, suc));
        break;

      case OR_MERGE:
        Log.error(String.format(errorMsg, "Backward", OR_MERGE));
        assert false;
    }

    Log.trace(String.format("Finish traversing %s forward", node.getLabel()), "");
  }
}
