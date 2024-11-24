package de.monticore.workflow.conformance.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.AbstractConfTest;
import de.monticore.workflow.conformance.ConformanceChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConformanceCheckerTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.init();
    // Log.initDEBUG();
  }

  @Test
  public void basicConformanceCheckerTest() {
    boolean res = checkConformance("Concrete", "Reference");
    Assertions.assertTrue(res);
  }

  public boolean checkConformance(String concrete, String reference) {
    String modelDir = "de.monticore.workflow.conformance.conf.";
    ASTWorkflowCompilationUnit con = loadModel(modelDir + concrete);
    ASTWorkflowCompilationUnit ref = loadModel(modelDir + reference);

    ConformanceChecker checker = new ConformanceChecker();
    return checker.checkConformance(con, ref);
  }
}
