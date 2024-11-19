package de.monticore.workflow.conformance;

import static de.monticore.workflow.conformance.utils.BPMNUtils.generateIDWfNode;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.conformance.ConfWfTraverser;
import de.monticore.workflow.conformance.conformance.ConfWfVisitor;
import de.monticore.workflow.conformance.conformance.PredicateGenerator;
import de.monticore.workflow.conformance.datastructure.interf.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.incarnation.IncarnationStrategy;
import de.monticore.workflow.conformance.incarnation.NameIncarnationStrategy;
import de.monticore.workflow.conformance.utils.CheckResult;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Predicate;

public class ConformanceChecker {

  private final String logName = this.getClass().getSimpleName();
  private IncarnationStrategy inc;
  private final Set<CheckResult> results = new HashSet<>();

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
    // todo fix  identifier for different strategies
    IDWfNodeBuilder refBuilder = generateIDWfNode(reference, i -> ":" + i);
    IDWfNodeBuilder conBuilder = generateIDWfNode(concrete, i -> ":" + i);

    inc = new NameIncarnationStrategy(refBuilder, conBuilder);

    for (WfNode con : conBuilder.getAllNodes()) {
      List<WfNode> references = inc.getReferenceElements(con);
      if (references.isEmpty()) {
        results.add(CheckResult.mkConform(con));
      } else if (references.size() == 1) {

        results.add(checkConformance(con, references.iterator().next()));
      } else {
        Log.error("Found more than one reference to the concrete element  " + conName);
      }
    }

    return checkAndPrintResult();
  }

  private boolean checkAndPrintResult() {
    Optional<CheckResult> nonConformedNode =
        results.stream()
            .filter(n -> n.getResult().equals(CheckResult.Result.NON_CONFORM))
            .findAny();

    for (CheckResult result : results) {
      Log.info(result.toString(), "");
    }

    return nonConformedNode.isEmpty();
  }

  CheckResult checkConformance(WfNode concrete, WfNode reference) {

    Predicate<List<WfNode>> refPredicate = PredicateGenerator.postPredicate(reference);

    ConfWfVisitor visitor = new ConfWfVisitor(concrete, refPredicate, inc);

    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(visitor, new ArrayList<>(), concrete);

    return visitor.getCheckResult();
  }
}
