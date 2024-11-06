package de.monticore.workflow.conformance;

import com.sun.source.tree.AssertTree;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGenerator;
import de.monticore.workflow.conformance.datastructure.ctl.PredicateGenerator;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Predicate;

class CTLGraphGeneratorTest extends AbstractConfTest {

 IdWfNode s ;
 IdWfNode t1 ;
 IdWfNode e ;

public void  initActivity(){

  ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.predicate.CTL");

   Assertions.assertTrue(IdWfNode.getNode("Start").isPresent());
   s = IdWfNode.getNode("Start").get();

  Assertions.assertTrue(IdWfNode.getNode("Task1").isPresent());
  t1 = IdWfNode.getNode("Task1").get();

  Assertions.assertTrue(IdWfNode.getNode("End").isPresent());
  e = IdWfNode.getNode("End").get();
}

  @BeforeEach
  public void setup() {
    init();
    Log.init();

    initActivity();


  }

  @Test
  public void testSimpleTask() {

    ASTWorkflowCompilationUnit con = loadModel("de.monticore.workflow.conformance.demo.CTL");

    CTLGenerator generator = new CTLGenerator();
    var graph = generator.bpmn2ctl(con);
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
    var graph = generator.bpmn2ctl(con);
  }
}
