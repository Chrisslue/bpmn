package de.monticore.workflow.conformance;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.workflow.conformance.datastructure.analysis.ConfWfNode;
import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.datastructure.analysis.WfGraphGenerator;
import org.junit.jupiter.api.Test;

import java.util.Set;

class WfConfDemoTest extends AbstractConfTest {

  @Test
  public void testSimpleTask() {
    ASTWorkflowCompilationUnit bpmn = loadModel("de.monticore.workflow.conformance.Workflow");
    WfGraphGenerator generator = new WfGraphGenerator();
    NodeBuilder<ConfWfNode> res = generator.buildBpmn(bpmn);


    WfNode nodeConcrete = null;
    WfNode nodeReference = null;
    Set<WfNode> directPredecessors= nodeConcrete.allPredecessor((egal , t) -> true, 1);

    for(WfNode pred: directPredecessors){
      nodeReference.existsPredecessor((egal, t) -> t == pred, 1).get();
    }
  }

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getFullChecker();
  }
}
