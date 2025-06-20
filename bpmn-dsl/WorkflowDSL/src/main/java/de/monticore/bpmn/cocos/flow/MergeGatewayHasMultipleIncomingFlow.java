/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 289 Description: A Gateway MUST have either
 * multiple incoming Sequence Flows xor multiple outgoing Sequence Flows (i.e., it MUST merge xor
 * split the flow).
 */
public class MergeGatewayHasMultipleIncomingFlow implements WorkflowASTWFGatewayCoCo {
  
  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.isConverging() && gateway.sizeIncomings() <= 1) {
      Log.error(Messages.get("0xWFM5002", gateway.getName()), gateway.get_SourcePositionStart(),
          gateway.get_SourcePositionEnd());
    }
  }
  
}
