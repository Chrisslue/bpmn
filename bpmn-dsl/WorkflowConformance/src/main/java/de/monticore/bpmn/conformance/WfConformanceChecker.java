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

  private Set<ConfUtils.WfConfParams> confParams = new HashSet<>();

  public WfConformanceChecker() {
    confParams.add(STEREOTYPES_MAPPING);
    confParams.add(NAME_MAPPING);
  }

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

  public IncarnationStrategy<WfNode> buildIncarnationStrategy(WfBuilder builder, String mapping) {
    ComposedIncStrategy incStrategy = new ComposedIncStrategy(builder, mapping);

    // in case conformance params strategies were not set
    if (confParams == null
        || (!confParams.contains(NAME_MAPPING) && !confParams.contains(STEREOTYPES_MAPPING))) {
      Log.info(
          "No mapping strategy not set. The Tool will use combined Name adn Stereotype Mapping",
          logger);
      incStrategy.addIncStrategy(new NameIncStrategy(builder));
      incStrategy.addIncStrategy(new StereotypesIncStrategy(builder, mapping));
    } else if (confParams.contains(NAME_MAPPING)) {
      incStrategy.addIncStrategy(new NameIncStrategy(builder));
    } else {
      incStrategy.addIncStrategy(new StereotypesIncStrategy(builder, mapping));
    }
    return incStrategy;
  }

  public void setConfParams(Set<ConfUtils.WfConfParams> confParams) {
    this.confParams = confParams;
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
