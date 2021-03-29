package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 178
 * Description: A Cancel End Event can only be used within a transaction Sub-Process.
 */
public class CancelEndEventIsContainedWithinTransaction implements WorkflowASTProcessCoCo, WorkflowASTSubProcessCoCo {

    @Override
    public void check(final ASTSubProcess subProcess) {
        if (subProcess.getType() == ASTSubProcessType.TRANSACTION) {
            return;
        }
        hasNoCancelEndEvent(subProcess);
    }

    @Override
    public void check(final ASTProcess process) {
        hasNoCancelEndEvent(process);
    }

    private void hasNoCancelEndEvent(final ASTFlowElementContainer container) {
        WorkflowCollectors.toEndEventsLocal(container).forEach(event -> {
            event.accept(new WorkflowVisitor() {
                @Override
                public void endVisit(ASTEventTriggerCancel node) {
                    Log.error(Messages.get("0xWFM2022", event.getName()),
                            event.get_SourcePositionStart(), event.get_SourcePositionEnd());
                }
            });
        });
    }

}
