 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.trafos;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/** 
 * TriggeredByEvent does not exist anymore, therefore is transformation is not used
*/

public class SetSubProcessTriggeredByEvent extends WorkflowTransformation {

  @Override
  protected void transform() {
    /*
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTWFSubProcess subProcess) {
            boolean triggeredByEvent =
                WorkflowCollectors.toStartEventsLocalSubProcess(subProcess).stream()
                    .filter(ASTWFEvent::isStart)
                    .anyMatch(ASTWFEvent::isPresentTrigger);

            subProcess.getSymbol().setTriggeredByEvent(triggeredByEvent);
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    getAst().accept(traverser);
    */
  }

}
