/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 289 Description: A Gateway with a
 * gatewayDirection of diverging MUST have multiple outgoing Sequence Flows, but MUST NOT have
 * multiple incoming Sequence Flows
 */
public class SplitGatewayHasAtMostOneIncomingFlow implements WorkflowASTWFGatewayCoCo {
  
  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.isDiverging() && gateway.sizeIncomings() > 1) {
      Log.error(Messages.get("0xWFM5003", gateway.getName()), gateway.get_SourcePositionStart(),
          gateway.get_SourcePositionEnd());
    }
  }
  
}
