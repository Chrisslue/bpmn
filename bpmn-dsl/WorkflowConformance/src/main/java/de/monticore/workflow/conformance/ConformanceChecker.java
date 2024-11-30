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
import java.util.stream.Collectors;

public class ConformanceChecker {

  private final String logger = "";
  private IncarnationStrategy inc;

  private final Set<CheckResult> checkResult = new HashSet<>();

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    String conName = concrete.getProcess().getName();
    String refName = reference.getProcess().getName();
    Log.println("");
    Log.info(
        String.format(
            "Start Checking Conformance of Concrete:%s to Reference:%s.", conName, refName),
        logger);

    // transform reference and concrete node
    // todo fix  identifier for different strategies
    IDWfNodeBuilder refBuilder = generateIDWfNode(reference, i -> ":" + i);
    IDWfNodeBuilder conBuilder = generateIDWfNode(concrete, i -> ":" + i);

    inc = new NameIncarnationStrategy(refBuilder, conBuilder);

    for (WfNode con : conBuilder.getAllNodes()) {
      List<WfNode> references = inc.getReferenceElements(con);

      if (references.isEmpty()) {
        checkResult.add(CheckResult.mkConform(con));
      } else if (references.size() == 1) {

        checkResult.add(checkConformance(con, references.get(0), conBuilder.getStartNodes()));
      } else {
        Log.error("Found more than one reference to the concrete element  " + conName);
      }
    }

    return printResult();
  }

  private boolean printResult() {
    List<WfNode> nonConform =
        checkResult.stream()
            .filter(n -> n.getResult().equals(CheckResult.Result.NON_CONFORM))
            .map(CheckResult::getNode)
            .collect(Collectors.toList());

    List<WfNode> unKnown =
        checkResult.stream()
            .filter(n -> n.getResult().equals(CheckResult.Result.NON_CONFORM))
            .map(CheckResult::getNode)
            .collect(Collectors.toList());

    Log.println("");
    Log.info("--- Final Result of Conformance Checking ---", logger);

    for (CheckResult result : checkResult) {
      Log.info(result.toString(), "");
    }

    if (!nonConform.isEmpty()) {
      Log.info("The following node are non conform: " + nonConform, logger);
      return false;
    }

    if (!unKnown.isEmpty()) {
      Log.info("The status of following node is unknown: " + nonConform, logger);
      return false;
    }

    Log.info("--- All node are Conformed to their reference ---", logger);
    return true;
  }

  protected CheckResult checkConformance(
      WfNode concrete, WfNode reference, Set<WfNode> conStartNodes) {

    Log.println("");
    Log.info(String.format("Checking Conformance of %s to %s", concrete, reference), logger);

    Predicate<List<WfNode>> refPredicate = PredicateGenerator.postPredicate(reference);

    ConfWfVisitor visitor = new ConfWfVisitor(concrete, conStartNodes, refPredicate, inc);

    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(visitor, null, concrete);

    visitor.printResult();

    return visitor.getResult();
  }
}
