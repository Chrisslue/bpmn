package de.monticore.workflow.conformance.utils;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public class CheckResult {

  private final WfNode node;
  private final Result result;
  private final Optional<List<WfNode>> branchId;

  private CheckResult(WfNode node, List<WfNode> branchId, Result result) {
    this.result = result;
    this.node = node;
    this.branchId = Optional.ofNullable(branchId);
  }

  public static CheckResult mkConform(WfNode node) {
    return new CheckResult(node, null, Result.CONFORM);
  }

  public static CheckResult mkNonConform(WfNode node, @Nonnull List<WfNode> branchId) {
    return new CheckResult(node, branchId, Result.NON_CONFORM);
  }

  public static CheckResult mkUnknown(WfNode node) {
    return new CheckResult(node, null, Result.UNKNOWN);
  }

  public Optional<List<WfNode>> getBranchId() {
    return branchId;
  }

  public Result getResult() {
    return result;
  }

  public WfNode getNode() {
    return node;
  }

  public enum Result {
    CONFORM,
    NON_CONFORM,
    UNKNOWN
  }

  @Override
  public String toString() {

    String branchId = getBranchId().isEmpty() ? "[]" : getBranchId().get().toString();
    return String.format("Node: %20s , result = %20s, witness = %s", node, result, branchId);
  }
}
