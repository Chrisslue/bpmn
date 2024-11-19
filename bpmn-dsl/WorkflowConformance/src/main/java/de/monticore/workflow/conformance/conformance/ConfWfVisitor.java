package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.incarnation.IncarnationStrategy;
import de.monticore.workflow.conformance.utils.CheckResult;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfWfVisitor implements WfNodeVisitor {
  private final Predicate<List<WfNode>> predicate;
  private final IncarnationStrategy inc;
  private final WfNode concreteNode;

  private CheckResult checkResult;

  public ConfWfVisitor(WfNode conNode, Predicate<List<WfNode>> predicate, IncarnationStrategy inc) {
    this.predicate = predicate;
    this.inc = inc;
    this.concreteNode = conNode;
    checkResult = CheckResult.mkConform(conNode);
  }

  @Override
  public boolean accept(WfNode node, List<WfNode> branchId) {
    branchId.add(node);

    Log.debug(String.format("Testing branch %s with predicate", branchId), "");

    List<WfNode> referenceNodes =
        branchId.stream()
            .map(inc::getReferenceElements)
            .flatMap(List::stream)
            .collect(Collectors.toList());

    boolean res = predicate.test(referenceNodes);

    if (!res && node.getSuccessors().isEmpty()) {
      checkResult = CheckResult.mkNonConform(concreteNode, branchId);
    }

    Log.debug(String.format("Test result: %S", res), "");
    return res;
  }

  public CheckResult getCheckResult() {
    return checkResult;
  }
}
