package de.monticore.workflow.conformance;

import static de.monticore.bpmn.conformance.datastructures.utils.CheckResult.Result.*;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CaseStudyTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.initDEBUG();
  }

  @Disabled
  @Test
  public void checkConformance() {

    // given
    String modelDir = "de.monticore.workflow.conformance.caseStudy.";

    ASTWorkflowCompilationUnit reference = loadModel(modelDir + "PaperAuthoringReferenceProcess");
    ASTWorkflowCompilationUnit concrete = loadModel(modelDir + "PaperAuthoringReferenceProcess");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    Assertions.assertTrue(currentResult);
  }
}
