package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowElementContainerCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: There are twelve types of
 * Intermediate Events in BPMN: None, Message, Timer, Escalation, Error, Cancel, Compensation,
 * Conditional, Link, Signal, Multiple, and Parallel Multiple.
 */
public class IntermediateCatchEventHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTFlowElementContainerCoCo {

  private static final String ERROR_CODE = "0xWFM2013";

  public IntermediateCatchEventHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTFlowElementContainer container) {
    WorkflowCollectors.toEventsLocal(container).stream()
        .filter(ASTEvent::isIntermediate)
        .filter(ASTEvent::isCatch)
        .filter(event -> !event.isBoundary())
        .forEach(this::check);
  }

  private void check(final ASTEvent event) {
    if (!event.isPresentTrigger()) {
      logError(event);
    }
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTEventTriggerEscalate trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerError trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerCancel trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTEventTriggerCompensate trigger) {
            logError(event);
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
