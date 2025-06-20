/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: There are twelve types of
 * Intermediate Events in BPMN: None, Message, Timer, Escalation, Error, Cancel, Compensation,
 * Conditional, Link, Signal, Multiple, and Parallel Multiple.
 */
public class IntermediateThrowEventHasValidTrigger extends AbstractHasValidTriggerCoCo implements
    WorkflowASTWFProcessCoCo {
  
  private static final String ERROR_CODE = "0xWFM2014";
  
  public IntermediateThrowEventHasValidTrigger() {
    super(ERROR_CODE);
  }
  
  @Override
  public void check(final ASTWFProcess container) {
    WorkflowCollectors.toEventsLocal(container).stream().filter(ASTWFEvent::isIntermediate).filter(
        ASTWFEvent::isThrow).forEach(this::check);
  }
  
  private void check(final ASTWFEvent event) {
    WorkflowVisitor2 visitor = new WorkflowVisitor2() {
      
      @Override
      public void visit(final ASTWFEventTriggerTimer trigger) {
        logError(event);
      }
      
      @Override
      public void visit(final ASTWFEventTriggerConditional trigger) {
        logError(event);
      }
      
      @Override
      public void visit(final ASTWFEventTriggerNotification trigger) {
        if (trigger.getType() == ASTConstantsWorkflow.ERROR) {
          logError(event);
        }
        
      }
      
      @Override
      public void visit(final ASTWFEventTriggerCancel trigger) {
        logError(event);
      }
      
      @Override
      public void visit(final ASTWFEventTriggerMultiple trigger) {
        if (!trigger.isParallelMultiple()) {
          logError(event);
        }
      }
      
      @Override
      public void visit(final ASTWFEventTriggerTerminate trigger) {
        logError(event);
      }
      
    };
    
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
  
}
