package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.BiPredicate;

public class ConfWfTraverser {

  Set<List<WfNode>> branchIdSet = new HashSet<>();

  private boolean result = true;

  public boolean getResult() {
    return result;
  }

  public Set<List<WfNode>> getBranchIdSet() {
    return branchIdSet;
  }

  public void traverseForward(
      BiPredicate<WfNode, List<WfNode>> wfVisitor, List<WfNode> branchId, WfNode node) {

    Log.debug(String.format("Start traversing %s forward", node.getLabel()), "");

    if (node.getSuccessors().isEmpty()) {
      var res = wfVisitor.test(node, branchId);
      Log.info(
          String.format("End of branch %s The result of this branch is %s", branchId, res), "");
      result = result && res;
    }

    switch (node.getNodeType()) {
      case EVENT:
      case TASK:
        wfVisitor.test(node, branchId);
        assert node.getSuccessors().size() == 1
            || node.getSuccessors().isEmpty(); // I don't know how to handle this otherwise
        // NO BREAK!
      case AND_SPLIT:
      case AND_MERGE:
      case XOR_MERGE:
        node.getSuccessors().forEach(suc -> this.traverseForward(wfVisitor, branchId, suc));
        branchIdSet.add(branchId);
        break;

      case XOR_SPLIT:
        branchIdSet.remove(branchId);
        for (WfNode suc : node.getSuccessors()) {
          var newBranchId = new ArrayList<>(branchId);
          this.traverseForward(wfVisitor, newBranchId, suc);
        }
        break;

      case OR_SPLIT:
      case OR_MERGE:
        assert false; // TODO implement me
    }

    Log.debug(String.format("Finish traversing %s forward", node.getLabel()), "");
  }
}
