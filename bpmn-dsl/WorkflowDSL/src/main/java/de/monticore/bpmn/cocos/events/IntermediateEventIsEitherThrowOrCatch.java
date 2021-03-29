package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 249
 * Description: The Event can respond to (“catch”) the Event trigger or the Event can be used to set off (“throw”) the Event trigger.
 */
public class IntermediateEventIsEitherThrowOrCatch implements WorkflowASTEventCoCo {

    @Override
    public void check(final ASTEvent event) {
        if (event.isIntermediate() && !event.isBoundary() && !event.isCatch() && !event.isThrow()) {
            Log.error(Messages.get("0xWFM2017", event.getName()),
                    event.get_SourcePositionStart(), event.get_SourcePositionEnd());
        }
    }

}
