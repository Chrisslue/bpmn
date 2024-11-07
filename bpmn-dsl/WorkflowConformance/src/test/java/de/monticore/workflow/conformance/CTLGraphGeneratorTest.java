package de.monticore.workflow.conformance;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGenerator;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CTLGraphGeneratorTest extends AbstractConfTest {

  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }

  @Test
  public void testSimpleTask() {

    ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.demo.CTL");

    CTLGenerator generator = new CTLGenerator();
    var graph = generator.buildCTL(con);
  }

  @Test
  public void testCTLGeneratorParallel() {

    ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.demo.Parallel");

    CTLGenerator generator = new CTLGenerator();
    var graph = generator.buildCTL(con);
    BPMNUtils.visualize(graph);
  }

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getFullChecker();
  }

  @Test
  public void createVariableTest() {
    ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.demo.CTL");
    CTLGenerator generator = new CTLGenerator();
    var graph = generator.buildCTL(con);
  }
}
