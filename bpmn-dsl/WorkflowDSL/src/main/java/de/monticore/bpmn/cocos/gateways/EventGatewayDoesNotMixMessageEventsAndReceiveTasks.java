package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 297 Description: If Message Intermediate
 * Events are used in the configuration, then Receive Tasks MUST NOT be used in that configuration
 * and vice versa.
 */
public class EventGatewayDoesNotMixMessageEventsAndReceiveTasks implements WorkflowASTGatewayCoCo {

  @Override
  public void check(final ASTGateway gateway) {
    if (gateway.getType().isEventBased()) {
      Collection<ASTEvent> messageEvents =
          gateway
              .streamOutgoings()
              .map(SequenceFlow::getTarget)
              .flatMap(WorkflowFilters::isEvent)
              .filter(ASTEvent::isIntermediate)
              .filter(ASTEvent::isPresentTrigger)
              .filter(event -> WorkflowFilters.getMessageTrigger(event.getTrigger()).isPresent())
              .collect(Collectors.toList());

      Collection<ASTTask> receiveTasks =
          gateway
              .streamOutgoings()
              .map(SequenceFlow::getTarget)
              .flatMap(WorkflowFilters::isTask)
              .filter(task -> task.getType() == ASTConstantsWorkflow.RECEIVE)
              .collect(Collectors.toList());

      if ((messageEvents.size() > 0) && (receiveTasks.size() > 0)) {
        messageEvents.forEach(
            event -> {
              Log.error(
                  Messages.get("0xWFM5008", gateway.getName()),
                  event.get_SourcePositionStart(),
                  event.get_SourcePositionEnd());
            });
        messageEvents.forEach(
            task -> {
              Log.error(
                  Messages.get("0xWFM5008", gateway.getName()),
                  task.get_SourcePositionStart(),
                  task.get_SourcePositionEnd());
            });
      }
    }
  }
}
