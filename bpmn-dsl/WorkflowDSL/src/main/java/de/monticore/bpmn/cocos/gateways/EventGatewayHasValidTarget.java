/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.gateways;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollector;
import de.monticore.bpmn.collectors.WorkflowFilter;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFGatewayCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 297 Description: Event-Based Gateways are
 * configured by having outgoing Sequence Flows target an Intermediate Event xor a Receive Task in
 * any combination. Only the following Intermediate Event triggers are valid: Message, Signal,
 * Timer, Conditional, and Multiple (which can only include the previous triggers).
 */
public class EventGatewayHasValidTarget implements WorkflowASTWFGatewayCoCo {
  
  @Override
  public void check(final ASTWFGateway gateway) {
    if (gateway.getType().isEventBased()) {
      gateway.streamOutgoings().map(SequenceFlow::getTarget).filter(this::isInvalidTarget).forEach(
          event -> Log.error(Messages.get("0xWFM5007", event.getName(), gateway.getName()), event
              .get_SourcePositionStart(), event.get_SourcePositionEnd()));
    }
  }
  
  private boolean isInvalidTarget(final ASTFlowElement flowNode) {
    WorkflowFilter<ASTFlowElement> filter = new WorkflowFilter<ASTFlowElement>(flowNode) {
      
      @Override
      public void visit(ASTWFTask node) {
        if (node.getType() == ASTConstantsWorkflow.RECEIVE) {
          select(node);
        }
      }
      
      @Override
      public void visit(ASTWFEvent node) {
        if (node.isIntermediate() && hasValidTrigger(node)) {
          select(node);
        }
      }
      
    };
    filter.filter(filter);
    
    return filter.getFiltered().isEmpty();
  }
  
  private boolean hasValidTrigger(final ASTWFEvent event) {
    // collect invalid triggers (multiple events may have nested triggers)
    WorkflowCollector<ASTWFEventTrigger> collector = new WorkflowCollector<ASTWFEventTrigger>(
        event) {
      
      @Override
      public void visit(final ASTWFEventTriggerNotification trigger) {
        if (trigger.getType() == ASTConstantsWorkflow.ERROR || trigger.getType()
            == ASTConstantsWorkflow.ESCALATION) {
          select(trigger);
        }
      }
      
      @Override
      public void visit(final ASTWFEventTriggerCancel trigger) {
        select(trigger);
      }
      
      @Override
      public void visit(final ASTWFEventTriggerCompensate trigger) {
        select(trigger);
      }
      
      @Override
      public void visit(final ASTWFEventTriggerTerminate trigger) {
        select(trigger);
      }
      
    };
    Collection<ASTWFEventTrigger> invalidTriggers = collector.collect(collector);
    
    return event.isPresentTrigger() && invalidTriggers.isEmpty();
  }
  
}
