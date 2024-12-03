package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.interf.WfNodeVisitor;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
// todo optimization possibilities: - stop traversing of all branches node when lower and upper are
// non conformed

public class ConfWfVisitor implements WfNodeVisitor {

  private final Predicate<List<WfNode>> predicate;

  private final IncarnationStrategy<WfNode> inc;

  private final WfNode node;

  private final Set<WfNode> startNodes;

  private boolean lowerBoundOnly = false;

  private boolean isBackward = false; // todo handle that differently

  private final Map<BranchID, CheckResult> lowerBoundResults;
  private final Map<BranchID, CheckResult> upperboundResults;

  private enum AbortRule {
    SATISFIED_PREDICATE,
    LOOP_DISCOVERED,
    END_NODE_REACHED,
    RETURN_TO_START
  }

  public ConfWfVisitor(
      WfNode conNode,
      Set<WfNode> startNodes,
      Predicate<List<WfNode>> predicate,
      IncarnationStrategy<WfNode> inc,
      boolean lowerBoundOnly) {
    this.predicate = predicate;
    this.inc = inc;
    this.node = conNode;
    this.startNodes = startNodes;
    this.upperboundResults = new HashMap<>();
    this.lowerBoundResults = new HashMap<>();
    this.lowerBoundOnly = lowerBoundOnly;
  }

  public void setBackward() {
    isBackward = true;
  }

  // todo break earlier when lower-bound only
  @Override
  public boolean accept(WfNode node, BranchID branchId) {
    /* if (lowerBoundResults.containsKey(branchId) && upperboundResults.containsKey(branchId)) {
      return false;
    }*/

    branchId.addNode(node);

    Log.debug(String.format("Testing branch %s with predicate", branchId), "");

    List<WfNode> referenceNodes = resolveReferenceNodes(branchId);

    boolean res = predicate.test(referenceNodes);
    if (!lowerBoundResults.containsKey(branchId)) {
      if (res) {
        Log.info(
            String.format(
                "Aborting lower-bound, branch %s, reason: %s",
                branchId, AbortRule.SATISFIED_PREDICATE),
            "");
        CheckResult checkResult = CheckResult.mkConform(this.node);
        lowerBoundResults.putIfAbsent(branchId, checkResult);
      }
    }
    if (branchId.isLoopDetected()) {
      Log.info(
          String.format(
              "Aborting lower and upper bound,  branch %s, reason: %s",
              branchId, AbortRule.LOOP_DISCOVERED),
          "");
      lowerBoundResults.putIfAbsent(branchId, CheckResult.mkConform(node));
      upperboundResults.putIfAbsent(branchId, CheckResult.mkConform(node));
      return false;
    }

    if (!startNodes.contains(this.node)
        && startNodes.contains(branchId.getNodeList().get(branchId.getNodeList().size() - 1))) {
      Log.info(
          String.format(
              "Aborting lower and upper bound, branch %s, reason: %s",
              branchId, AbortRule.RETURN_TO_START),
          "");
      CheckResult checkRes =
          res ? CheckResult.mkConform(this.node) : CheckResult.mkNonConform(this.node, branchId);
      lowerBoundResults.putIfAbsent(branchId, checkRes);
      upperboundResults.put(branchId, checkRes);
      return false;
    }

    if ((isBackward && node.getPredecessors().isEmpty())
        || (!isBackward && node.getSuccessors().isEmpty())) { // todo handel differently
      Log.info(
          String.format(
              "Aborting upper-bound,  branch %s, reason: %s", branchId, AbortRule.END_NODE_REACHED),
          "");

      CheckResult checkRes =
          res ? CheckResult.mkConform(this.node) : CheckResult.mkNonConform(this.node, branchId);
      lowerBoundResults.putIfAbsent(branchId, checkRes);
      upperboundResults.put(branchId, checkRes);
      return false;
    }

    return true;
  }

  public List<WfNode> resolveReferenceNodes(BranchID branchId) {
    List<WfNode> concreteNodeList = new ArrayList<>(branchId.getNodeList());
    concreteNodeList.remove(node);

    return concreteNodeList.stream()
        .map(inc::getReferenceElements)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  // todo  try to optimize it later
  public CheckResult getResult() {
    CheckResult lowerBoundRes = null;
    for (CheckResult res : lowerBoundResults.values()) {
      if (res.isNonConform()) {
        lowerBoundRes = res;
        break;
      }
    }

    if (lowerBoundRes == null) {
      lowerBoundRes = CheckResult.mkConform(node);
    }

    if (lowerBoundOnly) {
      return lowerBoundRes;
    }

    CheckResult upperBoundRes = null;
    for (CheckResult res : upperboundResults.values()) {
      if (res.isNonConform()) {
        upperBoundRes = res;
        break;
      }
    }

    // upper bound is CONFORM than lower bound is also CONFORM
    if (upperBoundRes == null) {
      return CheckResult.mkConform(node);
    }

    // upper-bound NON-CONFORM && lower-bound NON-CONFORM
    if (lowerBoundRes.isConform()) {
      return CheckResult.mkUnknown(node, upperBoundRes.getBranchId().get());
    } else {
      return CheckResult.mkNonConform(node, upperBoundRes.getBranchId().get());
    }
  }

  public void printResult() {

    Log.println("");

    Log.info("---------- Lower bound Results: ---------", "");

    for (var res : lowerBoundResults.entrySet()) {
      Log.info(String.format("branch: %s, res= %s", res.getKey(), res.getValue().getResult()), "");
    }

    Log.println("");

    if (!lowerBoundOnly) {
      Log.info("---------- upperbound Results: ----------", "");

      for (var res : upperboundResults.entrySet()) {
        Log.info(
            String.format("branch: %s, res= %s", res.getKey(), res.getValue().getResult()), "");
      }
    }
  }
}
