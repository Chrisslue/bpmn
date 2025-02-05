package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTWFEventTriggerCancel;
import de.monticore.bpmn.workflow._ast.ASTWFEventTriggerTerminate;
import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 241 Description: The same Event types as for
 * boundary Events are allowed, namely: Message, Timer, Escalation, Error, Compensation,
 * Conditional, Signal, Multiple, and Parallel.
 */
public class StartEventSubProcessHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTWFSubProcessCoCo {
      
  private static final String ERROR_CODE = "0xWFM2011";

  public StartEventSubProcessHasValidTrigger() {
    super(ERROR_CODE);
  }
  
  @Override
  public void check(final ASTWFSubProcess subProcess) {
    /*
    if (subProcess.getSymbol().isTriggeredByEvent()) {
      WorkflowCollectors.toEventsLocalSubProcess(subProcess).stream()
          .filter(ASTWFEvent::isStart)
          .forEach(this::check);
    }
    */
  }

  private void check(final ASTWFEvent event) {
    if (!event.isPresentTrigger()) {
      logError(event);
    }
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTWFEventTriggerCancel trigger) {
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
