package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowElementContainerCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 246
 * Description: There are nine types of End Events in BPMN: None, Message, Escalation, Error, Cancel, Compensation, Signal, Terminate, and Multiple.
 */
public class EndEventHasValidTrigger extends AbstractHasValidTriggerCoCo implements WorkflowASTFlowElementContainerCoCo {

    private static final String ERROR_CODE = "0xWFM2012";

    public EndEventHasValidTrigger() {
        super(ERROR_CODE);
    }

    @Override
    public void check(final ASTFlowElementContainer container) {
        WorkflowCollectors.toEventsLocal(container)
                .stream()
                .filter(ASTEvent::isEnd)
                .forEach(this::check);
    }

    private void check(final ASTEvent event) {
        WorkflowVisitor2 visitor = new WorkflowVisitor2() {
            @Override
            public void visit(final ASTEventTriggerTimer trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerConditional trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerMultiple trigger) {
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
