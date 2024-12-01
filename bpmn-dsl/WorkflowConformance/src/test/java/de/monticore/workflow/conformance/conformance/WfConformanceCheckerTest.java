package de.monticore.workflow.conformance.conformance;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.AbstractConfTest;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WfConformanceCheckerTest extends AbstractConfTest {
  ASTWorkflowCompilationUnit concrete;
  ASTWorkflowCompilationUnit reference;
  private final String modelDir = "de.monticore.workflow.conformance.conf.";

  @BeforeEach
  public void setup() {
    init();
    Log.init();
    // Log.initDEBUG();

  }

  @Test
  public void testXorConformance() {

    // given
    concrete = parse_str("process Concrete { event start S; task T1; S -> T1;}");
    reference = loadModel(modelDir + "xor.Reference");

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean checkRes = checker.checkConformance(concrete, reference, "ref");

    // Then
    Assertions.assertTrue(checkRes);
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
    return new WfConformanceChecker().checkConformance(con, ref, "ref");
  }
}
