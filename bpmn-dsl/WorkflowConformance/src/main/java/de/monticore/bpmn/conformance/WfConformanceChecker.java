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
  private ASTWorkflowCompilationUnit concrete;
  private ASTWorkflowCompilationUnit reference;
  private ComposedIncStrategy incarnationStrategy;

  private final Set<CheckResult> checkResult = new HashSet<>();
  private final Set<ConfUtils.WfConfParams> confParams = new HashSet<>();

  public WfConformanceChecker() {
    confParams.add(STEREOTYPES_MAPPING);
    confParams.add(NAME_MAPPING);
  }

  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference, String mapping) {
    this.concrete = concrete;
    this.reference = reference;
    Log.info(
        String.format(
            "Checking Conformance of [Concrete:%s] to [Reference:%s]\n",
            concrete.getProcess().getName(), reference.getProcess().getName()),
        "");

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
    CTLConfStrategy nodeChecker = new CTLConfStrategy(conBuilder, refBuilder, incarnationStrategy);

    // check conformance for non-gateway nodes
    for (var con : conBuilder.getAllNodes()) {
      if (!con.getNodeType().isGateway()) {
        checkResult.add(nodeChecker.checkConformance(con));
      }
    }

    // check and print the results
    return printCheckResults();
  }

  private IncarnationStrategy<WfNode> buildIncarnationStrategy(WfBuilder builder, String mapping) {
    this.incarnationStrategy = new ComposedIncStrategy(builder, mapping);

    // in case conformance params strategies were not set
    incarnationStrategy.addIncStrategy(new StereotypesIncStrategy(builder, mapping));
    if (confParams.contains(NAME_MAPPING)) {
      incarnationStrategy.addIncStrategy(new NameIncStrategy(builder));
    }

    return incarnationStrategy;
  }

  public void addConfParams(ConfUtils.WfConfParams param) {
    this.confParams.add(param);
  }

  public Set<CheckResult> getCheckResult() {
    return checkResult;
  }

  private boolean printCheckResults() {
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

    Log.info("--- Final Result of Conformance Checking ---", "");

    if (!nonConform.isEmpty()) {
      Log.info("The following nodes do not conform: " + nonConform + "\n", "");
    }

    if (!unKnown.isEmpty()) {
      Log.info("The status of the following nodes is unknown: " + unKnown + "\n", "");
    }

    if (unKnown.isEmpty() && nonConform.isEmpty()) {
      Log.info("--- All nodes conform to their reference ---\n", "");
    }

    Log.info("-------- Explanations --------: \n", "");
    for (CheckResult result : checkResult) {
      String con = concrete.getProcess().getName();
      String ref = reference.getProcess().getName();

      if (result.getResult().equals(CheckResult.Result.NON_CONFORM)) {
        var refNode = this.incarnationStrategy.getReferenceElements(result.getNode()).get(0);
        Log.info(
            String.format(
                "Result: Node [%s:%s] does not conform to Node [%s:%s]",
                con, result.getNode(), ref, refNode),
            "");
        String run;
        if (result.isBackwards()) {
          run = "backtrack";
        }
        else {
          run = "run";
        }
        Log.info(
                String.format(
                        "Counter example: The following %s %s is possible in [%s] but not in [%s].\n",
                        run, result.printWitness(), con, ref),
                  "");

      }

      if (result.getResult().equals(CheckResult.Result.UNKNOWN)) {
        var refNode = this.incarnationStrategy.getReferenceElements(result.getNode()).get(0);
        Log.info(
            String.format(
                "Result: Node [%s:%s] may not conform to Node [%s:%s]",
                con, result.getNode(), ref, refNode),
            "");
        Log.info(
            String.format(
                "Counter example: The following run %s is possible in [%s] but may not be possible in [%s].\n",
                result.printWitness(), con, ref),
            "");
      }
    }
    System.out.println("\n\n");
    return nonConform.isEmpty() && unKnown.isEmpty();
  }

  public List<WfNode> getNonConformNodes() {
    return checkResult.stream()
        .filter(n -> n.getResult().equals(CheckResult.Result.NON_CONFORM))
        .map(CheckResult::getNode)
        .collect(Collectors.toList());
  }

  public List<WfNode> getUnknownNodes() {
    return checkResult.stream()
        .filter(n -> n.getResult().equals(CheckResult.Result.UNKNOWN))
        .map(CheckResult::getNode)
        .collect(Collectors.toList());
  }
}
