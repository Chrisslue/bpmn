package de.monticore.workflow.conformance;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WfConfDemoTest extends AbstractConfTest {

  @BeforeEach
  public  void setup(){
    init();
  }

  @Test
  public void testSimpleTask() {
    ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.demo.Concrete");
    ASTWorkflowCompilationUnit ref = loadModel("de.monticore.workflow.conformance.demo.Reference");

    WfConformanceChecker checker = new WfConformanceChecker();
    Assertions.assertTrue(checker.checkConformance(con, ref));
  }

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getFullChecker();
  }
}
