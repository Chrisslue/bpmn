package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: There are twelve types of
 * Intermediate Events in BPMN: None, Message, Timer, Escalation, Error, Cancel, Compensation,
 * Conditional, Link, Signal, Multiple, and Parallel Multiple.
 */
public class IntermediateThrowEventHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTProcessCoCo {

  private static final String ERROR_CODE = "0xWFM2014";

  public IntermediateThrowEventHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTProcess container) {
    WorkflowCollectors.toEventsLocal(container).stream()
        .filter(ASTEvent::isIntermediate)
        .filter(ASTEvent::isThrow)
        .forEach(this::check);
  }

  private void check(final ASTEvent event) {
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTEventTriggerTimer trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerConditional trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerNotification trigger) {
            if(trigger.getType() == ASTConstantsWorkflow.ERROR){
              logError(event);
            }
            
          }

          @Override
          public void visit(final ASTEventTriggerCancel trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerMultiple trigger) {
            if (!trigger.isParallelMultiple()) {
              logError(event);
            }
          }

          @Override
          public void visit(final ASTEventTriggerTerminate trigger) {
            logError(event);
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
}
