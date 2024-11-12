package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConfWfVisitor {
  private final Predicate<List<WfNode>> predicate;
  private final IncarnationStrategy inc;

  Set<List<WfNode>> branchIdSet = new HashSet<>();

  private boolean result = true;

  public boolean getResult() {
    return result;
  }

  public ConfWfVisitor(Predicate<List<WfNode>> predicate, IncarnationStrategy inc) {
    this.predicate = predicate;
    this.inc = inc;
  }

  public boolean accept(WfNode node, List<WfNode> branchId) {
    branchId.add(node);

    Log.info(String.format("Testing branch %s with predicate", branchId), "");

    List<WfNode> referenceNodes =
        branchId.stream()
            .map(inc::getReferenceElements)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());

    boolean res = predicate.test(referenceNodes);

    if (node.getSuccessors().isEmpty()) {
      result = res && result;
      branchIdSet.add(branchId);
    }

    Log.info(String.format("Test result: %S", res), "");
    return res;
  }

  public String printStatistics() {
    return String.format("A total of %s branch Visited", branchIdSet.size());
  }
}
