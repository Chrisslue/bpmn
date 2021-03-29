package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 258
 * Description: An Intermediate Event MUST be a source for a Sequence Flow. Multiple Sequence Flows MAY originate from an Intermediate Event.
 */
public class IntermediateEventHasOneOrMoreOutgoingFlows implements WorkflowASTEventCoCo {

    @Override
    public void check(final ASTEvent event) {
        if (event.isIntermediate() && event.isEmptyOutgoings()) {
            Log.error(Messages.get("0xWFM2021", event.getName()),
                    event.get_SourcePositionStart(), event.get_SourcePositionEnd());
        }
    }
}
