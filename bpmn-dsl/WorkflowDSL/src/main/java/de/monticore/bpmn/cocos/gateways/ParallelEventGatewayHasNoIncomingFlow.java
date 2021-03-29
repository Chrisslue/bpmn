package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 298
 * Description: If the Event Gateway’s instantiate attribute is set to true and the eventGatewayType attribute is set to Parallel, [...]
 */
public class ParallelEventGatewayHasNoIncomingFlow implements WorkflowASTGatewayCoCo {

    @Override
    public void check(final ASTGateway gateway) {
        if (gateway.getType().isParallelEventBased() && !gateway.isEmptyIncomings()) {
            Log.error(Messages.get("0xWFM5012", gateway.getName()),
                    gateway.get_SourcePositionStart(), gateway.get_SourcePositionEnd());
        }
    }

}
