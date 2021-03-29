package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 258
 * Description: If the Intermediate Event is attached to the boundary of an Activity:
 * The Intermediate Event MUST NOT be a target for a Sequence Flow; it cannot have an incoming Sequence Flows
 */
public class BoundaryEventHasNoIncomingFlow implements WorkflowASTEventCoCo {

    @Override
    public void check(final ASTEvent event) {
        if (event.isBoundary() && !event.isEmptyIncomings()) {
            Log.error(Messages.get("0xWFM2005", event.getName()), event.get_SourcePositionStart(), event.get_SourcePositionEnd());
        }
    }

}
