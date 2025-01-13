package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;
// todo optimization possibilities: - stop traversing of all branches node when lower and upper are
// non conformed

public class BranchVisitor {

  private final WfPredicate predicate;

  private final IncarnationStrategy<WfNode> inc;

  private final WfNode node;

  private final Set<WfNode> startNodes;

  private boolean lowerBoundOnly = false;

  private final Map<BranchID, CheckResult> lowerBoundResults;
  private final Map<BranchID, CheckResult> upperboundResults;

  private Set<BranchID> branchIDSet = new HashSet<>();

  private BranchVisitor(
      WfNode conNode,
      Set<WfNode> startNodes,
      WfPredicate predicate,
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

  public static BranchVisitor mkForwardVisitor(
      WfNode conNode,
      Set<WfNode> startNodes,
      WfPredicate predicate,
      IncarnationStrategy<WfNode> inc) {
    return new BranchVisitor(conNode, startNodes, predicate, inc, false);
  }

  public static BranchVisitor mkBackwardVisitor(
      WfNode conNode,
      Set<WfNode> startNodes,
      WfPredicate predicate,
      IncarnationStrategy<WfNode> inc) {
    return new BranchVisitor(conNode, startNodes, predicate, inc, true);
  }

  // todo break earlier when lower-bound only

  public boolean accept(BranchID branchId) {

    branchIDSet.add(branchId);

    Log.debug(String.format("Testing branch %s with predicate [%s]", branchId, predicate), "");

    List<WfNode> referenceNodes = resolveReferenceNodes(branchId);
    boolean res = predicate.test(referenceNodes);
    Log.trace("Result:" + res, "");
    if (!lowerBoundResults.containsKey(branchId)) {
      if (res) {
        Log.trace(
            String.format(
                "Aborting lower-bound, branch %s, reason: %s",
                branchId, AbortReason.SATISFIED_PREDICATE),
            "");
        CheckResult checkResult = CheckResult.mkConform(this.node);
        lowerBoundResults.putIfAbsent(branchId, checkResult);
      }
    }

    if (branchId.isLoopDetected()) {
      Log.info(
          String.format(
              "Aborting lower and upper bound,  branch %s, reason: %s",
              branchId, AbortReason.LOOP_DISCOVERED),
          "");
      lowerBoundResults.putIfAbsent(branchId, CheckResult.mkConform(node));
      upperboundResults.putIfAbsent(branchId, CheckResult.mkConform(node));
      return false;
    }

    if (!startNodes.contains(this.node)
        && !branchId.getNodeList().isEmpty()
        && startNodes.contains(branchId.getNodeList().get(branchId.getNodeList().size() - 1))) {
      Log.trace(
          String.format(
              "Aborting lower and upper bound, branch %s, reason: %s",
              branchId, AbortReason.RETURN_TO_START),
          "");
      Log.trace("Result:" + res, "");
      CheckResult checkRes =
          res ? CheckResult.mkConform(this.node) : CheckResult.mkNonConform(this.node, branchId);
      lowerBoundResults.putIfAbsent(branchId, checkRes);
      upperboundResults.put(branchId, checkRes);
      return false;
    }

    return true;
  }

  public boolean abort() {
    branchIDSet = branchIDSet.stream().filter(n -> !n.isAborted()).collect(Collectors.toSet());
    for (var branchId : branchIDSet) {

      branchIDSet.add(branchId);
      List<WfNode> referenceNodes = resolveReferenceNodes(branchId);
      boolean res = predicate.test(referenceNodes);
      Log.trace(
          String.format(
              "Aborting upper-bound and lower,  branch %s, reason: %s",
              branchId, AbortReason.END_NODE_REACHED),
          "");

      CheckResult checkRes =
          res ? CheckResult.mkConform(this.node) : CheckResult.mkNonConform(this.node, branchId);
      lowerBoundResults.putIfAbsent(branchId, checkRes);
      upperboundResults.put(branchId, checkRes);
    }
    return false;
  }

  public List<WfNode> resolveReferenceNodes(BranchID branchId) {
    List<WfNode> concreteNodeList = new ArrayList<>(branchId.getNodeList());

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
