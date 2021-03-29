package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 29
 * Description: Start events can only react to ("catch") a trigger
 */
public class StartEventIsNotThrowing implements WorkflowASTEventCoCo {

    @Override
    public void check(final ASTEvent node) {
        if (node.isStart() && node.isThrow()) {
            Log.error(Messages.get("0xWFM2001", node.getName()),
                    node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
    }

}
