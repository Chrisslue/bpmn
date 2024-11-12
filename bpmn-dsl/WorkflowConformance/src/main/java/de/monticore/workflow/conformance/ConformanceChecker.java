package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.conformance.ConfWfTraverser;
import de.monticore.workflow.conformance.conformance.PredicateGenerator;
import de.monticore.workflow.conformance.datastructure.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.incarnation.NameIncarnationStrategy;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConformanceChecker {

  private final String logName = this.getClass().getSimpleName();

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    String conName = concrete.getProcess().getName();
    String refName = reference.getProcess().getName();
    Log.println("");
    Log.info(
        String.format(
            "Start Checking Conformance of Concrete:%s to Reference:%s.", conName, refName),
        logName);

    // transform reference and concrete node
    IDWfNodeBuilder ref =
        BPMNUtils.generateIDWfNode(
            reference, i -> ":" + i); // todo fix  identifier for different strategies
    IDWfNodeBuilder con = BPMNUtils.generateIDWfNode(concrete, i -> ":" + i);
    NameIncarnationStrategy inc = new NameIncarnationStrategy(ref, con);

    Predicate<List<WfNode>> refPredicate = PredicateGenerator.postPredicate(ref.getStartEvent());

    BiPredicate<WfNode, List<WfNode>> visitor =
        (node, branchId) -> {
          branchId.add(node);
          Log.info(String.format("Testing branch %s with predicate", branchId), logName);

          List<WfNode> referenceNodes =
              branchId.stream()
                  .map(inc::getReference)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .collect(Collectors.toList());

          boolean res = refPredicate.test(referenceNodes);
          Log.info(String.format("Test result: %S", res), logName);

          return res;
        };

    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(visitor, new ArrayList<>(), con.getStartEvent());

    Log.info(
        String.format(
            "End of the conformance check of Concrete:%s to Reference:%s.", conName, refName),
        logName);

    Log.info(
        String.format("A total of %s branches visited", traverser.getBranchIdSet().size()),
        logName);
    Log.println("");
    return traverser.getResult();
  }
}
