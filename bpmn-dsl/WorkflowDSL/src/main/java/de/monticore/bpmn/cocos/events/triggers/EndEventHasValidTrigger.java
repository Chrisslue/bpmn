 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 246 Description: There are nine types of End
 * Events in BPMN: None, Message, Escalation, Error, Cancel, Compensation, Signal, Terminate, and
 * Multiple.
 */
public class EndEventHasValidTrigger extends AbstractHasValidTriggerCoCo
    implements WorkflowASTWFProcessCoCo {

  private static final String ERROR_CODE = "0xWFM2012";

  public EndEventHasValidTrigger() {
    super(ERROR_CODE);
  }

  @Override
  public void check(final ASTWFProcess container) {
    WorkflowCollectors.toEventsLocal(container).stream()
        .filter(ASTWFEvent::isEnd)
        .forEach(this::check);
  }

  private void check(final ASTWFEvent event) {
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTWFEventTriggerTimer trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTWFEventTriggerConditional trigger) {
            logError(event);
          }

          @Override
          public void visit(final ASTWFEventTriggerMultiple trigger) {
            if (!trigger.isParallelMultiple()) {
              logError(event);
            }
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
}
