package de.monticore.workflow.conformance;


import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CaseStudyTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.initDEBUG();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "PaperAuthoring",
        "Sequential",
        "SequentialWithLoop",
        "AddingNewTasks",
        "MultipleIncarnation"
      })
  public void checkConformance(String con) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.conform.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoring");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + con);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }

  @Test
  @Disabled
  public void checkConfornknknknkknknknknknknknknknmance() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoringReferenceProcess");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + "");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }
}
