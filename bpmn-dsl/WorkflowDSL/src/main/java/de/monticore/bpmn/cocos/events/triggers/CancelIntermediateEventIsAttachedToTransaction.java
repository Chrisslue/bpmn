package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTEventTriggerCancel;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 178
 * Description: Cancel Intermediate Event can only be used when attached to the boundary of a Transaction Sub-Process. It cannot be used in any normal flow and cannot be attached to a non-Transaction Sub-Process.
 */
public class CancelIntermediateEventIsAttachedToTransaction implements WorkflowASTProcessCoCo, WorkflowASTSubProcessCoCo {

    @Override
    public void check(final ASTSubProcess subProcess) {
        List<ASTEvent> events = WorkflowCollectors.toEventsLocal(subProcess);
        events.forEach(event -> {
            if (!subProcess.isTransaction() || !event.isBoundary()) {
                logErrorIfCancelIntermediateEvent(event);
            }
        });
    }

    @Override
    public void check(final ASTProcess process) {
        List<ASTEvent> events = WorkflowCollectors.toEventsLocal(process);
        events.forEach(this::logErrorIfCancelIntermediateEvent);
    }

    private void logErrorIfCancelIntermediateEvent(final ASTEvent event) {
        if (!event.isIntermediate()) {
            return;
        }
        WorkflowVisitor2 visitor = new WorkflowVisitor2() {
            @Override
            public void endVisit(ASTEventTriggerCancel node) {
                Log.error(Messages.get("0xWFM2023", event.getName()),
                        event.get_SourcePositionStart(), event.get_SourcePositionEnd());
            }
        };

        WorkflowTraverser traverser = WorkflowMill.traverser();
        traverser.add4Workflow(visitor);
        event.accept(traverser);
    }

}
