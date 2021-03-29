package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 262
 * Description: The catch Compensation Intermediate Event MUST only be attached to the boundary of an Activity and,
 * thus, MAY NOT be used in normal flow.
 */
public class CompensateCatchEventIsNotPartOfNormalFlow implements WorkflowASTEventCoCo {

    @Override
    public void check(final ASTEvent event) {
        event.accept(new WorkflowVisitor() {
            @Override
            public void visit(final ASTEventTriggerCompensate trigger) {
                if (event.isBoundary() && !event.isEmptyOutgoings()) {
                    Log.error(Messages.get("0xWFM2024", event.getName()),
                            event.get_SourcePositionStart(), event.get_SourcePositionEnd());
                }
            }
        });
    }

}
