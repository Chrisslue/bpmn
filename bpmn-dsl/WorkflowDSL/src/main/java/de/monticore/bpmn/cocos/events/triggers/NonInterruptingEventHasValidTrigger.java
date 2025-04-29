 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

public class NonInterruptingEventHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTWFEventCoCo {

  private static final String ERROR_CODE = "0xWFM2019";

  public NonInterruptingEventHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTWFEvent event) {
    if (!event.isNoninterrupt()) {
      return;
    }

    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTWFEventTriggerNotification trigger) {
            if(trigger.getType() == ASTConstantsWorkflow.ERROR){
              logError(event);
            }
          }

          @Override
          public void visit(final ASTWFEventTriggerCancel trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTWFEventTriggerCompensate trigger) {
            logError(event);
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
}
