package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 289
 * Description: A Gateway with a gatewayDirection of converging MUST have multiple incoming Sequence Flows,
 * but MUST NOT have multiple outgoing Sequence Flows
 */
public class MergeGatewayHasAtMostOneOutgoingFlow implements WorkflowASTGatewayCoCo {

    @Override
    public void check(final ASTGateway gateway) {
        if (gateway.isConverging() && gateway.sizeOutgoings() > 1) {
            Log.error(Messages.get("0xWFM5004", gateway.getName()),
                    gateway.get_SourcePositionStart(), gateway.get_SourcePositionEnd());
        }
    }

}
