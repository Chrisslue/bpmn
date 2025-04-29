 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 239 Description: There are seven (7) types of
 * Start Events for top-level Processes in BPMN (see Table 10.84): None, Message, Timer,
 * Conditional, Signal, Multiple, and Parallel.
 */
public class StartEventTopLevelProcessHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTWFProcessCoCo {

  private static final String ERROR_CODE = "0xWFM2010";

  public StartEventTopLevelProcessHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTWFProcess process) {
    WorkflowCollectors.toEventsLocal(process).stream()
        .filter(ASTWFEvent::isStart)
        .forEach(this::check);
  }

  private void check(final ASTWFEvent event) {
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTWFEventTriggerNotification trigger) {
            if(trigger.getType() == ASTConstantsWorkflow.ESCALATION || trigger.getType() == ASTConstantsWorkflow.ERROR){
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

          @Override
          public void visit(final ASTWFEventTriggerTerminate trigger) {
            logError(event);
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
}
