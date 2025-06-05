 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._ast.ASTConstantsWorkflow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 297 Description: Receive Tasks used in an
 * Event Gateway configuration MUST NOT have any attached Intermediate Events.
 */
public class EventGatewayTargetReceiveTaskHasNoBoundaryEvents implements WorkflowASTWFGatewayCoCo {

  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.getType().isEventBased()) {
      gateway
          .streamOutgoings()
          .map(SequenceFlow::getTarget)
          .flatMap(WorkflowFilters::isTask)
          .filter(task -> task.getType() == ASTConstantsWorkflow.RECEIVE)
          .filter(receiveTask -> !receiveTask.isEmptyBoundaryEvent())
          .forEach(
              receiveTask ->
                  Log.error(
                      Messages.get("0xWFM5009", receiveTask.getName()),
                      receiveTask.get_SourcePositionStart(),
                      receiveTask.get_SourcePositionEnd()));
    }
  }
}
