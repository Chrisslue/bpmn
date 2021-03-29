package de.monticore.bpmn.trafos;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

/**
 * Updates {@code triggeredByEvent} of event sub-processes.
 */
public class SetSubProcessTriggeredByEvent extends WorkflowTransformation {

    @Override
    protected void transform() {
        getAst().accept(new WorkflowVisitor() {
            @Override
            public void visit(final ASTSubProcess subProcess) {
                boolean triggeredByEvent = WorkflowCollectors.toStartEventsLocal(subProcess)
                        .stream()
                        .filter(ASTEvent::isStart)
                        .anyMatch(ASTEvent::isPresentTrigger);

                subProcess.setTriggeredByEvent(triggeredByEvent);
            }
        });
    }

}
