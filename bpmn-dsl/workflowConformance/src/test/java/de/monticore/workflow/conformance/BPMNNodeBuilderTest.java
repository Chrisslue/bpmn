package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.BPMNNode;
import org.junit.jupiter.api.Test;

class BPMNNodeBuilderTest {

  private String baseDir = "src/test/resources/de/monticore/workflow/conformance/";

  @Test
  public void testSimpleTask() {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit bpmn = tool.parse(baseDir + "Workflow.wfm");
    tool.createSymbolTable(bpmn);
    WF2LTSGenerator generator = new WF2LTSGenerator();
    BPMNNode res = generator.bpmn2lts(bpmn);

    System.out.println(res);
  }
}
