/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.utils;

import de.monticore.bpmn.collectors.WorkflowFilter;
import de.monticore.bpmn.workflow._ast.*;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Utilities to avoid casting in streams
 *
 * <p>Usage: flowNodes.stream().flatMap(WorkflowFilters::isTask).forEach(...)
 */
public class WorkflowFilters {
  
  public static Stream<ASTWFTask> isTask(final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFTask> filter = new WorkflowFilter<ASTWFTask>(node) {
      
      @Override
      public void visit(ASTWFTask node) {
        select(node);
      }
      
    };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }
  
  public static Stream<ASTWFEvent> isEvent(final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFEvent> filter = new WorkflowFilter<ASTWFEvent>(node) {
      
      @Override
      public void visit(ASTWFEvent node) {
        select(node);
      }
      
    };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }
  
  public static Stream<ASTWFGateway> isGateway(final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFGateway> filter = new WorkflowFilter<ASTWFGateway>(node) {
      
      @Override
      public void visit(ASTWFGateway node) {
        select(node);
      }
      
    };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }
  
  public static Stream<ASTWFEventTriggerNotification> isMessageTrigger(final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFEventTriggerNotification> filter =
        new WorkflowFilter<ASTWFEventTriggerNotification>(node) {
          
          @Override
          public void visit(ASTWFEventTriggerNotification node) {
            if (node.getKind() == ASTConstantsWorkflow.MESSAGE) {
              select(node);
            }
          }
          
        };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }
  
  public static Optional<ASTWFEventTriggerNotification> getMessageTrigger(
      final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFEventTriggerNotification> filter =
        new WorkflowFilter<ASTWFEventTriggerNotification>(node) {
          
          @Override
          public void visit(ASTWFEventTriggerNotification node) {
            if (node.getKind() == ASTConstantsWorkflow.MESSAGE) {
              select(node);
            }
          }
          
        };
    filter.filter(filter);
    return filter.getFiltered();
  }
  
  public static boolean isCompensateTrigger(final ASTWorkflowNode node) {
    WorkflowFilter<ASTWFEventTriggerCompensate> filter =
        new WorkflowFilter<ASTWFEventTriggerCompensate>(node) {
          
          @Override
          public void visit(ASTWFEventTriggerCompensate node) {
            select(node);
          }
          
        };
    filter.filter(filter);
    return filter.getFiltered().isPresent();
  }
  
}
