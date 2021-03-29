package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

public class NonInterruptingEventHasValidTrigger extends AbstractHasValidTriggerCoCo implements WorkflowASTEventCoCo {

    private static final String ERROR_CODE = "0xWFM2019";

    public NonInterruptingEventHasValidTrigger() {
        super(ERROR_CODE);
    }

    @Override
    public void check(final ASTEvent event) {
        if (!event.isNonInterrupt()) {
            return;
        }

        event.accept(new WorkflowVisitor() {
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
        });
    }

}
