package de.monticore.bpmn.trafos;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/** Updates {@code triggeredByEvent} of event sub-processes. */
public class SetSubProcessTriggeredByEvent extends WorkflowTransformation {

  @Override
  protected void transform() {
    WorkflowVisitor2 visitor =
        new WorkflowVisitor2() {
          @Override
          public void visit(final ASTSubProcess subProcess) {
            boolean triggeredByEvent =
                WorkflowCollectors.toStartEventsLocal(subProcess).stream()
                    .filter(ASTEvent::isStart)
                    .anyMatch(ASTEvent::isPresentTrigger);

            subProcess.setTriggeredByEvent(triggeredByEvent);
          }
        };

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    getAst().accept(traverser);
  }
}
