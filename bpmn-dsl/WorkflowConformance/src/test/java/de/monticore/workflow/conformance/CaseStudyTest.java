package de.monticore.workflow.conformance;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import java.util.List;
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
    // Log.initDEBUG();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "conform.Sequential",
        "conform.SequentialWithLoop",
        "conform.AddingNewTasks",
        "nonconform.AntiPattern",
        "nonconform.WrongSequentialOrder",
        "PaperAuthoring"
        //  "conform.MultipleIncarnation" todo there is a problem with predicate builder
      })
  public void checkReflexiveConformance(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";
    ASTWorkflowCompilationUnit reference = loadModel(modelDir + model);
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, null);

    // Then
    Assertions.assertTrue(currentResult);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "conform.Sequential",
        "conform.SequentialWithLoop",
        "conform.AddingNewTasks",
        "conform.MultipleIncarnation"
      })
  public void checkConformance(String model) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }

  public static Stream<Arguments> nonConform() {
    return Stream.of(
        Arguments.of("AntiPattern", Set.of("Draft", "Introduction", "Conclusion", "Main")),
        Arguments.of("WrongSequentialOrder", Set.of("Draft", "Research")),
        Arguments.of("TaskNotIncarnated", Set.of("Draft", "Conclusion", "Introduction", "Main")));
  }

  @ParameterizedTest
  @MethodSource("nonConform")
  public void checkNonConformance(String model, Set<String> tasks) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + "nonconform." + model);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertFalse(currentResult);

    List<String> nonConformedNode =
        checker.getNonConformNodes().stream().map(WfNode::getLabel).collect(Collectors.toList());
    tasks.forEach(task -> Assertions.assertTrue(nonConformedNode.contains(task)));
  }

  @Test
  public void checkConfornknknknkknknknknknknknknknmance() { // todo remove me later
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + "nonconform.TaskNotIncarnated");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertFalse(currentResult);
  }
}
