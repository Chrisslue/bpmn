package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class ConfWfTraverser {

  public void traverseForward(ConfWfVisitor wfVisitor, List<WfNode> branchId, WfNode node) {

    Log.debug(String.format("Traversing node %s forward", node), "");

    switch (node.getNodeType()) {
      case EVENT:
      case TASK:
        boolean abort = wfVisitor.accept(node, branchId);
        if (abort) {
          return;
        }
        assert node.getSuccessors().size() <= 1;
        node.getSuccessors().forEach(suc -> this.traverseForward(wfVisitor, branchId, suc));
        break;

      case AND_SPLIT:
      case AND_MERGE:
      case XOR_MERGE:
        node.getSuccessors().forEach(suc -> this.traverseForward(wfVisitor, branchId, suc));
        break;

      case XOR_SPLIT:
        for (WfNode suc : node.getSuccessors()) {
          var newBranchId = new ArrayList<>(branchId);
          branchId.add(suc);
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
