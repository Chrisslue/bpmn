package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.WfNodeGenerator;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

public class WfConformanceChecker {

  private DummyIncarnationStrategy incStrategy = new DummyIncarnationStrategy();



  /**
   * procedure to check if a node conform
   */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit reference, ASTWorkflowCompilationUnit concrete) {

    Log.info("Checking conformance of Concrete.wfm to Reference.wfm",this.getClass().getName());

    // transform reference and concrete node
    WfNodeGenerator generator = new WfNodeGenerator();
    WfNode ref = generator.generateNode(reference);
    WfNode con = generator.generateNode(concrete);


   //checking conformance of start nodes
    Log.info("Checking conformance of Start nodes",this.getClass().getName());
    Set<WfNode> directSuccessors = con.allSuccessors((path, t) -> true, 1);

    for (WfNode pred : directSuccessors) {
     Optional<WfNode> refNode = ref.existsSuccessor((path, t) -> incStrategy.isIncarnation( t,pred), 1);
     if (refNode.isEmpty()){
       Log.info("Start nodes does not conform",this.getClass().getName());
       return false;
     }
    }

    Log.info("Start nodes conforms", this.getClass().getName());

    return true;
  }






}
