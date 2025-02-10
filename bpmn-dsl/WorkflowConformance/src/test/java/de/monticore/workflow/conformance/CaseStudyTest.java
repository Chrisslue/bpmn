package de.monticore.workflow.conformance;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CaseStudyTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "conform.Sequential",
        "conform.SequentialWithLoop",
        "conform.AddingNewTasks",
        "conform.Thesis",
        "nonconform.AntiPatternMerge",
        "nonconform.AntiPatternSplit",
        "nonconform.WrongSequentialOrder",
        "PaperAuthoring"
      })
  public void checkReflexiveConformance(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";
    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + model);
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, null);

    // Then
    Assertions.assertTrue(currentResult);
  }

  @Test
  public void testSkipSelf() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";
    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "Skip");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "Skip");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, null);

    // Then
    Assertions.assertFalse(currentResult);

    Set<String> unknownNodes =
        checker.getUnknownNodes().stream().map(WfNode::getLabel).collect(Collectors.toSet());
    Assertions.assertEquals(Set.of("A"), unknownNodes);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "conform.Thesis",
        "conform.Sequential",
        "conform.SequentialWithLoop",
        "conform.AddingNewTasks",
        "conform.MultipleIncarnation",
        "conform.MultipleIncarnation2",
        "conform.MultipleIncarnation3"
      })
  public void checkConformance(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }

  public static Stream<Arguments> nonConform() {
    return Stream.of(
        Arguments.of("AntiPatternMerge", Set.of("Review")),
        Arguments.of("AntiPatternSplit", Set.of("Expose", "Review")),
        Arguments.of("WrongSequentialOrder", Set.of("Draft", "Research")),
        Arguments.of("TaskNotIncarnated", Set.of("Draft", "Introduction", "Review")));
  }

  @ParameterizedTest
  @MethodSource("nonConform")
  public void checkNonConformance(String model, Set<String> tasks) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "nonconform." + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertFalse(currentResult);

    Set<String> nonConformedNode =
        checker.getNonConformNodes().stream().map(WfNode::getLabel).collect(Collectors.toSet());
    Assertions.assertEquals(tasks, nonConformedNode);
  }

  @Test
  public void CheckRemoveLoop() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.SequentialWithLoop");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "conform.Sequential");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }

  @ParameterizedTest
  @ValueSource(
          strings = {
                  "conform.MultipleIncarnation2",
                  "conform.MultipleIncarnation3"
          })
  public void removeAlternatives(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + model);
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "conform.MultipleIncarnation");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertTrue(currentResult);
  }

  @ParameterizedTest
  @ValueSource(
          strings = {
                  "conform.MultipleIncarnation3",
                  "conform.MultipleIncarnation4"
          })
  public void CheckInclusiveToOther(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation2");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertTrue(currentResult);
  }

  @ParameterizedTest
  @ValueSource(
          strings = {
                  "conform.MultipleIncarnation2",
                  "conform.MultipleIncarnation4"
          })
  public void CheckExclusiveToOther(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation3");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertFalse(currentResult);
  }

  @ParameterizedTest
  @ValueSource(
          strings = {
                  "conform.MultipleIncarnation2",
                  "conform.MultipleIncarnation3"
          })
  public void CheckParallelToOther(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation4");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertFalse(currentResult);
  }

  @Test
  public void CheckXORAntiPattern() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation3");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "more.MIXORAnti");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertFalse(currentResult);
  }

  @Test
  public void CheckANDAntiPattern() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation4");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "more.MIANDAnti");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertFalse(currentResult);
  }

  @Test
  public void CheckXOR2Sequence() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadBPMN(modelDir + "conform.MultipleIncarnation3");
    ASTWorkflowCompilationUnit concrete = loadBPMN(modelDir + "more.MIXOR2Seq");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "incarnates");

    Assertions.assertFalse(currentResult);
  }


}
