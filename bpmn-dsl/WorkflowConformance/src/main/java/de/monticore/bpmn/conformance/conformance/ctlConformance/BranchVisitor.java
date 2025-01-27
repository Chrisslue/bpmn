package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class BranchVisitor {

  private final WfPredicate predicate;
  private final IncarnationStrategy<WfNode> incarnationStrategy;
  private final WfNode branchOrigin;
  private final Set<WfNode> bpmnStartNodes;
  private final boolean lowerBoundOnly;
  private Set<BranchID> branchIDSet = new HashSet<>();

  private BranchVisitor(
      WfNode conNode,
      Set<WfNode> bpmnStartNodes,
      WfPredicate predicate,
      IncarnationStrategy<WfNode> incarnationStrategy,
      boolean lowerBoundOnly) {
    this.predicate = predicate;
    this.incarnationStrategy = incarnationStrategy;
    this.branchOrigin = conNode;
    this.bpmnStartNodes = bpmnStartNodes;

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

  public boolean accept(BranchID branchId) {
    if (branchId.isCheckAborted() || branchId.isCheckCompleted()) {
      return false;
    }
    branchIDSet.add(branchId);

    Log.trace(String.format("Checking branch %s with predicate [%s]", branchId, predicate), "");

    List<WfNode> referenceNodes = resolveReferenceNodes(branchId);
    boolean res = predicate.test(referenceNodes);

    Log.trace("Result:" + res, "");

    if (res) {
      Log.trace(
          String.format(
              "Aborting lower-bound, branch %s, reason: %s",
              branchId, AbortReason.SATISFIED_PREDICATE),
          "");
      CheckResult checkResult = CheckResult.mkConform(this.branchOrigin);
      branchId.setLoweBoundResult(checkResult);
    }

    if (new HashSet<>(branchId.getNodeList()).size() < branchId.getNodeList().size()) {
      Log.info(
          String.format(
              "Aborting lower and upper bound,  branch %s, reason: %s",
              branchId, AbortReason.LOOP_DISCOVERED),
          "");

      branchId.setLoweBoundResult(CheckResult.mkConform(branchOrigin));
      branchId.setUpperBoundResult(CheckResult.mkConform(branchOrigin));
      branchId.completeCheck();
      return false;
    }

    if (!bpmnStartNodes.contains(this.branchOrigin)
        && !branchId.getNodeList().isEmpty()
        && bpmnStartNodes.contains(branchId.getNodeList().get(branchId.getNodeList().size() - 1))) {
      Log.trace(
          String.format(
              "Aborting lower and upper bound, branch %s, reason: %s",
              branchId, AbortReason.RETURN_TO_START),
          "");
      Log.trace("Result:" + res, "");

      CheckResult checkRes =
          res
              ? CheckResult.mkConform(this.branchOrigin)
              : CheckResult.mkNonConform(this.branchOrigin, branchId);
      branchId.setLoweBoundResult(checkRes);
      branchId.setUpperBoundResult(checkRes);
      branchId.completeCheck();
      return false;
    }

    return true;
  }

  public void abort() {
    branchIDSet = branchIDSet.stream().filter(n -> !n.isCheckAborted()).collect(Collectors.toSet());
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
          res
              ? CheckResult.mkConform(this.branchOrigin)
              : CheckResult.mkNonConform(this.branchOrigin, branchId);
      branchId.setLoweBoundResult(checkRes);
      branchId.setUpperBoundResult(checkRes);
      branchId.completeCheck();
    }
  }

  public List<WfNode> resolveReferenceNodes(BranchID branchId) {
    List<WfNode> concreteNodeList = new ArrayList<>(branchId.getNodeList());

    return concreteNodeList.stream()
        .map(incarnationStrategy::getReferenceElements)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  // todo  try to optimize it later
  public CheckResult getResult() {
    CheckResult lowerBoundRes = null;
    for (BranchID res : branchIDSet) {
      if (res.getLoweBoundResult().isNonConform()) {
        lowerBoundRes = res.getLoweBoundResult();
        break;
      }
    }

    if (lowerBoundRes == null) {
      lowerBoundRes = CheckResult.mkConform(branchOrigin);
    }

    if (lowerBoundOnly) {
      return lowerBoundRes;
    }

    CheckResult upperBoundRes = null;
    for (var res : branchIDSet) {
      if (res.getUpperBoundResult().isNonConform()) {
        upperBoundRes = res.getUpperBoundResult();
        break;
      }
    }

    // upper bound is CONFORM than lower bound is also CONFORM
    if (upperBoundRes == null) {
      return CheckResult.mkConform(branchOrigin);
    }

    // upper-bound NON-CONFORM && lower-bound NON-CONFORM
    if (lowerBoundRes.isConform()) {
      return CheckResult.mkUnknown(branchOrigin, upperBoundRes.getBranchId().get());
    } else {
      return CheckResult.mkNonConform(branchOrigin, upperBoundRes.getBranchId().get());
    }
  }

  public void printResult() {

    Log.trace("", "");
    Log.trace("------------------------ Predicate ------------------------", "");
    Log.trace(predicate.toString(), "");
    Log.trace("---------- Lower bound Results: ---------", "");

    for (var res : branchIDSet) {
      Log.trace(
          String.format("branch: %s, res= %s", res, res.getLoweBoundResult().getResult()), "");
    }

    Log.trace("", "");

    if (!lowerBoundOnly) {
      Log.trace("---------- upperbound Results: ----------", "");

      for (var res : branchIDSet) {
        Log.trace(
            String.format("branch: %s, res= %s", res, res.getUpperBoundResult().getResult()), "");
      }
    }
  }
}
