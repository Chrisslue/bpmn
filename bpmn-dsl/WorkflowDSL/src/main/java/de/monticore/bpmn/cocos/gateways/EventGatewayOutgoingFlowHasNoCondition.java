package de.monticore.bpmn.cocos.gateways;


import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 296
 * Description: The outgoing Sequence Flows of the Event Gateway MUST NOT have a conditionExpression
 */
public class EventGatewayOutgoingFlowHasNoCondition implements WorkflowASTGatewayCoCo {

    @Override
    public void check(final ASTGateway gateway) {
        if (gateway.getType().isEventBased()) {
            gateway.streamOutgoings()
                    .map(SequenceFlow::getConditions)
                    .flatMap(Collection::stream)
                    .forEach(condition -> Log.error(Messages.get("0xWFM5006", gateway.getName()),
                            condition.get_SourcePositionStart(), condition.get_SourcePositionEnd()));
        }
    }

}
