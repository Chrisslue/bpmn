package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTEventTriggerTerminate;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: There are twelve types of
 * Intermediate Events in BPMN: None, Message, Timer, Escalation, Error, Cancel, Compensation,
 * Conditional, Link, Signal, Multiple, and Parallel Multiple.
 */
public class BoundaryEventHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTEventCoCo {

  private static final String ERROR_CODE = "0xWFM2016";

  public BoundaryEventHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTEvent event) {
    if (!event.isBoundary()) {
      return;
    }

    if (!event.isPresentTrigger()) {
      logError(event);
    }

    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
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
