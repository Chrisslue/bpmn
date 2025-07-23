/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 296 Description: An Event Gateway MUST have
 * two xor more outgoing Sequence Flows
 */
public class EventGatewayHasTwoOrMoreOutgoingFlows implements WorkflowASTWFGatewayCoCo {
  
  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.getType().isEventBased() && gateway.getOutgoingsList().size() < 2) {
      Log.error(Messages.get("0xWFM5005", gateway.getName()), gateway.get_SourcePositionStart(),
          gateway.get_SourcePositionEnd());
    }
  }
  
}
