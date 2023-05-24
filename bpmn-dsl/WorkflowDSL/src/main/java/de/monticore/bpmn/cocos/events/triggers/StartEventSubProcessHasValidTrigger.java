package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTEventTriggerCancel;
import de.monticore.bpmn.workflow._ast.ASTEventTriggerTerminate;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 241 Description: The same Event types as for
 * boundary Events are allowed, namely: Message, Timer, Escalation, Error, Compensation,
 * Conditional, Signal, Multiple, and Parallel.
 */
public class StartEventSubProcessHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTSubProcessCoCo {

  private static final String ERROR_CODE = "0xWFM2011";

  public StartEventSubProcessHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTSubProcess subProcess) {
    if (subProcess.isTriggeredByEvent()) {
      WorkflowCollectors.toEventsLocal(subProcess).stream()
          .filter(ASTEvent::isStart)
          .forEach(this::check);
    }
  }

  private void check(final ASTEvent event) {
    if (!event.isPresentTrigger()) {
      logError(event);
    }
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTEventTriggerCancel trigger) {
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
