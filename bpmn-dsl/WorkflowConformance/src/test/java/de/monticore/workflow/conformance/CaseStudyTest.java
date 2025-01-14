package de.monticore.workflow.conformance;

import static de.monticore.bpmn.conformance.datastructures.utils.CheckResult.Result.*;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class CaseStudyTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.initDEBUG();
  }

  @ParameterizedTest
  @ValueSource(strings = {"PaperAuthoringReferenceProcess","PaperAuthoringConcreteProcess1","SequentialWithLoop"})
  public void checkConformance(String con) {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoringReferenceProcess");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir +con);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }

@Test
  public void checkConfornknknknkknknknknknknknknknmance() {
    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoringReferenceProcess");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + "SequentialWithLoop");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }
}
