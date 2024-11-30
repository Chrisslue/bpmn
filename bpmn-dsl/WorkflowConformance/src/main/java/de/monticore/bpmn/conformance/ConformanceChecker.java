package de.monticore.bpmn.conformance;



import de.monticore.bpmn.conformance.conformance.ctlConformance.CTLConfStrategy;
import de.monticore.bpmn.conformance.datastructures.IDWfNodeBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.WfNodeFactory;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.monticore.bpmn.conformance.incarnation.NameIncStrategy;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class ConformanceChecker {

  private final String logger = "";

    private final Set<CheckResult> checkResult = new HashSet<>();

  /** procedure to check if a node conforms */
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

    WfBuilder refBuilder = new IDWfNodeBuilder(i -> ":" + i);
      WfNodeFactory.buildWorkflowNodes(reference,refBuilder);

    IDWfNodeBuilder conBuilder = new IDWfNodeBuilder(i -> ":" + i);
    WfNodeFactory.buildWorkflowNodes(reference,conBuilder);

      IncarnationStrategy<WfNode> incarnationStrategy = new NameIncStrategy(refBuilder);

    CTLConfStrategy checker = new CTLConfStrategy(conBuilder,refBuilder, incarnationStrategy);

    for (var con : conBuilder.getAllNodes()) {
      if (!con.getNodeType().isGateway()){
        checkResult.add(checker.checkConformance(con));
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



}
