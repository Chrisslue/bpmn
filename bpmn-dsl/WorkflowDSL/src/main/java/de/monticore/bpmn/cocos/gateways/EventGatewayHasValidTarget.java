package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollector;
import de.monticore.bpmn.collectors.WorkflowFilter;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 297 Description: Event-Based Gateways are
 * configured by having outgoing Sequence Flows target an Intermediate Event xor a Receive Task in
 * any combination. Only the following Intermediate Event triggers are valid: Message, Signal,
 * Timer, Conditional, and Multiple (which can only include the previous triggers).
 */
public class EventGatewayHasValidTarget implements WorkflowASTGatewayCoCo {

  @Override
  public void check(final ASTGateway gateway) {
    if (gateway.getType().isEventBased()) {
      gateway
          .streamOutgoings()
          .map(SequenceFlow::getTarget)
          .filter(this::isInvalidTarget)
          .forEach(
              event ->
                  Log.error(
                      Messages.get("0xWFM5007", event.getName(), gateway.getName()),
                      event.get_SourcePositionStart(),
                      event.get_SourcePositionEnd()));
    }
  }

  private boolean isInvalidTarget(final ASTFlowNode flowNode) {
    WorkflowFilter<ASTFlowNode> filter =
        new WorkflowFilter<ASTFlowNode>(flowNode) {
          @Override
          public void visit(ASTTask node) {
            if (node.isPresentType() && node.getType() == ASTTaskType.RECEIVE) {
              select(node);
            }
          }

          @Override
          public void visit(ASTEvent node) {
            if (node.isIntermediate() && hasValidTrigger(node)) {
              select(node);
            }
          }
        };
    filter.filter(filter);

    return filter.getFiltered().isEmpty();
  }

  private boolean hasValidTrigger(final ASTEvent event) {
    // collect invalid triggers (multiple events may have nested triggers)
    WorkflowCollector<ASTEventTrigger> collector =
        new WorkflowCollector<>(event) {
          @Override
          public void visit(final ASTEventTriggerEscalate trigger) {
            select(trigger);
          }

          @Override
          public void visit(final ASTEventTriggerError trigger) {
            select(trigger);
          }

          @Override
          public void visit(final ASTEventTriggerCancel trigger) {
            select(trigger);
          }

          @Override
          public void visit(final ASTEventTriggerCompensate trigger) {
            select(trigger);
          }

          @Override
          public void visit(final ASTEventTriggerTerminate trigger) {
            select(trigger);
          }
        };
    Collection<ASTEventTrigger> invalidTriggers = collector.collect(collector);

    return event.isPresentTrigger() && invalidTriggers.isEmpty();
  }
}
