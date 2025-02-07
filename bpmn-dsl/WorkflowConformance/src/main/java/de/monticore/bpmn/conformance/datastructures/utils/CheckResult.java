package de.monticore.bpmn.conformance.datastructures.utils;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.swing.*;

public class CheckResult {

  private final WfNode node;
  private final Result result;
  private final Optional<BranchID> branchId;
  private final boolean backwards;

  private CheckResult(WfNode node, BranchID branchId, Result result) {
    this.result = result;
    this.node = node;
    this.branchId = Optional.ofNullable(branchId);
    this.backwards = false;
  }

  private CheckResult(WfNode node, BranchID branchId, Result result, boolean backwards) {
    this.result = result;
    this.node = node;
    this.branchId = Optional.ofNullable(branchId);
      this.backwards = backwards;
  }

  public String printWitness() {
    return branchId.get().getNodeList().toString();
  }

  public static CheckResult mkConform(WfNode node) {
    return new CheckResult(node, null, Result.CONFORM);
  }

  public static CheckResult mkNonConform(WfNode node, @Nonnull BranchID branchId) {
    return new CheckResult(node, branchId, Result.NON_CONFORM);
  }

  public static CheckResult mkNonConformBW(WfNode node, @Nonnull BranchID branchId) {
    return new CheckResult(node, branchId, Result.NON_CONFORM, true);
  }

  public static CheckResult mkUnknown(WfNode node, BranchID branchId) {
    return new CheckResult(node, branchId, Result.UNKNOWN);
  }

  public Optional<BranchID> getBranchId() {
    return branchId;
  }

  public Result getResult() {
    return result;
  }

  public WfNode getNode() {
    return node;
  }

  public boolean isConform() {
    return result == Result.CONFORM;
  }

  public boolean isNonConform() {
    return result == Result.NON_CONFORM;
  }

  public boolean isUnknown() {
    return result == Result.UNKNOWN;
  }

  public boolean isBackwards() {
    return backwards;
  }

  public enum Result {
    CONFORM,
    NON_CONFORM,
    UNKNOWN
  }

  @Override
  public String toString() {
    String branchId = getBranchId().isEmpty() ? "[]" : getBranchId().get().toString();
    return String.format("Node: %s , result = %s, witness = %s", node, result, branchId);
  }
}
