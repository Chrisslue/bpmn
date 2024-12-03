package de.monticore.bpmn.conformance;

import static de.monticore.bpmn.conformance.datastructures.utils.ConfUtils.WfConfParams.NAME_MAPPING;
import static de.monticore.bpmn.conformance.datastructures.utils.ConfUtils.WfConfParams.STEREOTYPES_MAPPING;

import de.monticore.bpmn.conformance.conformance.ctlConformance.CTLConfStrategy;
import de.monticore.bpmn.conformance.datastructures.WfNodeFactory;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.datastructures.utils.ConfUtils;
import de.monticore.bpmn.conformance.incarnation.ComposedIncStrategy;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.monticore.bpmn.conformance.incarnation.NameIncStrategy;
import de.monticore.bpmn.conformance.incarnation.StereotypesIncStrategy;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Collectors;

public class WfConformanceChecker {

  private final String logger = "";

  private final Set<CheckResult> checkResult = new HashSet<>();

  private final Set<ConfUtils.WfConfParams> confParams = new HashSet<>();

  public WfConformanceChecker() {
    confParams.add(STEREOTYPES_MAPPING);
    confParams.add(NAME_MAPPING);
  }
  // todo  return a CheckResult.Result instead ?
  /** procedure to check if a node conforms */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference, String mapping) {

    Log.println("");
    Log.info("Start Checking Conformance of Concrete to Reference", logger);

    // reference model &&  concrete model
    WfBuilder refBuilder;
    WfBuilder conBuilder;
    if (confParams.contains(NAME_MAPPING)) {
      refBuilder = WfNodeFactory.workflowBuilder(reference, "");
      conBuilder = WfNodeFactory.workflowBuilder(concrete, "");
    } else {
      refBuilder = WfNodeFactory.workflowBuilder(reference, ConfUtils.REFERENCE_PREFIX);
      conBuilder = WfNodeFactory.workflowBuilder(concrete, ConfUtils.CONCRETE_PREFIX);
    }

    // build incarnation Strategy
    IncarnationStrategy<WfNode> incarnationStrategy = buildIncarnationStrategy(refBuilder, mapping);

    // build conformance strategy
    CTLConfStrategy checker = new CTLConfStrategy(conBuilder, refBuilder, incarnationStrategy);

    // check conformance for non-gateway nodes
    for (var con : conBuilder.getAllNodes()) {
      if (!con.getNodeType().isGateway()) {
        checkResult.add(checker.checkConformance(con));
      }
    }

    // check and print the results
    return checkAndPrintResults();
  }

  private IncarnationStrategy<WfNode> buildIncarnationStrategy(WfBuilder builder, String mapping) {
    ComposedIncStrategy incStrategy = new ComposedIncStrategy(builder, mapping);

    // in case conformance params strategies were not set
    incStrategy.addIncStrategy(new StereotypesIncStrategy(builder, mapping));
    if (confParams.contains(NAME_MAPPING)) {
      incStrategy.addIncStrategy(new NameIncStrategy(builder));
    }

    return incStrategy;
  }

  public void addConfParams(ConfUtils.WfConfParams param) {
    this.confParams.add(param);
  }

  public Set<CheckResult> getCheckResult() {
    return checkResult;
  }

  private boolean checkAndPrintResults() {
    List<WfNode> nonConform =
        checkResult.stream()
            .filter(n -> n.getResult().equals(CheckResult.Result.NON_CONFORM))
            .map(CheckResult::getNode)
            .collect(Collectors.toList());

    List<WfNode> unKnown =
        checkResult.stream()
            .filter(n -> n.getResult().equals(CheckResult.Result.UNKNOWN))
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
