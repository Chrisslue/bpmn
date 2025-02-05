package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 297 Description: Target elements in an Event
 * Gateway configuration MUST NOT have any additional incoming Sequence Flows (other than that from
 * the Event Gateway)
 */
public class EventGatewayTargetHasNoAdditionalIncomingFlow implements WorkflowASTWFGatewayCoCo {

  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.getType().isEventBased()) {
      gateway
          .streamOutgoings()
          .map(SequenceFlow::getTarget)
          .forEach(
              target ->
                  target
                      .streamIncomings()
                      .map(SequenceFlow::getSource)
                      .filter(source -> !source.equals(gateway))
                      .forEach(
                          source ->
                              Log.error(
                                  Messages.get("0xWFM5011", target.getName()),
                                  source.get_SourcePositionStart(),
                                  source.get_SourcePositionEnd())));
    }
  }
}
