package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.interf.WfNodeVisitor;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
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

  private final Map<BranchID, CheckResult> lowerBoundResults;
  private final Map<BranchID, CheckResult> upperboundResults;

  public ConfWfVisitor(
      WfNode conNode,
      Set<WfNode> startNodes,
      Predicate<List<WfNode>> predicate,
      IncarnationStrategy<WfNode> inc) {
    this.predicate = predicate;
    this.inc = inc;
    this.node = conNode;
    this.startNodes = startNodes;
    this.upperboundResults = new HashMap<>();
    this.lowerBoundResults = new HashMap<>();
  }

  @Override
  public boolean accept(WfNode node, BranchID branchId) {
    if (lowerBoundResults.containsKey(branchId) && upperboundResults.containsKey(branchId)) {
      return false;
    }

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
              branchId, AbortRule.SATISFIED_PREDICATE),
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
      upperboundResults.putIfAbsent(branchId, checkRes);
      return false;
    }

    if (node.isEnd() | node.getSuccessors().isEmpty()) {
      Log.info(
          String.format(
              "Aborting upper-bound,  branch %s, reason: %s", branchId, AbortRule.END_NODE_REACHED),
          "");

      CheckResult checkRes =
          res ? CheckResult.mkConform(this.node) : CheckResult.mkNonConform(this.node, branchId);
      lowerBoundResults.putIfAbsent(branchId, checkRes);
      upperboundResults.putIfAbsent(branchId, checkRes);
      return false;
    }

    return true;
  }

  public List<WfNode> resolveReferenceNodes(BranchID branchId) {
    return branchId.getNodeList().stream()
        .map(inc::getReferenceElements)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public void printResult() {
    Log.println("");
    Log.info(String.format("--- Result for forward traversing of Node %s --- ", node), "");

    Log.println("");

    Log.info("---------- Lower bound Results: ---------", "");

    for (var res : lowerBoundResults.entrySet()) {
      Log.info(String.format("branch: %s, res= %s", res.getKey(), res.getValue().getResult()), "");
    }

    Log.println("");

    Log.info("---------- upperbound Results: ----------", "");

    for (var res : upperboundResults.entrySet()) {
      Log.info(String.format("branch: %s, res= %s", res.getKey(), res.getValue().getResult()), "");
    }
  }

  public CheckResult getResult() {
    CheckResult lowerBound = CheckResult.mkConform(this.node);
    CheckResult upperBound = CheckResult.mkConform(this.node);

    for (CheckResult res : lowerBoundResults.values()) {
      if (res.isConform()) {
        lowerBound = res;
      }
    }

    for (CheckResult res : upperboundResults.values()) {
      if (res.isNonConform()) {
        upperBound = res;
      }
    }

    if (upperBound.isConform()) {
      return upperBound;
    }

    if (lowerBound.isConform()) {
      return CheckResult.mkUnknown(this.node, upperBound.getBranchId().get());
    }
    return upperBound;
  }

  private enum AbortRule {
    SATISFIED_PREDICATE,
    LOOP_DISCOVERED,
    END_NODE_REACHED,
    RETURN_TO_START
  }
}
