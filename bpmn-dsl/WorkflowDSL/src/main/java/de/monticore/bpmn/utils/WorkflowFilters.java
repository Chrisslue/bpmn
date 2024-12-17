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

  public static Stream<ASTTask> isTask(final ASTWorkflowNode node) {
    WorkflowFilter<ASTTask> filter =
        new WorkflowFilter<ASTTask>(node) {
          @Override
          public void visit(ASTTask node) {
            select(node);
          }
        };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }

  public static Stream<ASTEvent> isEvent(final ASTWorkflowNode node) {
    WorkflowFilter<ASTEvent> filter =
        new WorkflowFilter<ASTEvent>(node) {
          @Override
          public void visit(ASTEvent node) {
            select(node);
          }
        };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }

  public static Stream<ASTGateway> isGateway(final ASTWorkflowNode node) {
    WorkflowFilter<ASTGateway> filter =
        new WorkflowFilter<ASTGateway>(node) {
          @Override
          public void visit(ASTGateway node) {
            select(node);
          }
        };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }

  public static Stream<ASTEventTriggerNotification> isMessageTrigger(final ASTWorkflowNode node) {
    WorkflowFilter<ASTEventTriggerNotification> filter =
        new WorkflowFilter<ASTEventTriggerNotification>(node) {
          @Override
          public void visit(ASTEventTriggerNotification node) {
            if(node.getType() == ASTConstantsWorkflow.MESSAGE){
              select(node);
            }
          }
        };
    filter.filter(filter);
    return Stream.of(filter.getFiltered()).filter(Optional::isPresent).map(Optional::get);
  }

  public static Optional<ASTEventTriggerNotification> getMessageTrigger(final ASTWorkflowNode node) {
    WorkflowFilter<ASTEventTriggerNotification> filter =
        new WorkflowFilter<ASTEventTriggerNotification>(node) {
          @Override
          public void visit(ASTEventTriggerNotification node) {
            if(node.getType() == ASTConstantsWorkflow.MESSAGE){
              select(node);
            }
          }
        };
    filter.filter(filter);
    return filter.getFiltered();
  }

  public static boolean isCompensateTrigger(final ASTWorkflowNode node) {
    WorkflowFilter<ASTEventTriggerCompensate> filter =
        new WorkflowFilter<ASTEventTriggerCompensate>(node) {
          @Override
          public void visit(ASTEventTriggerCompensate node) {
            select(node);
          }
        };
    filter.filter(filter);
    return filter.getFiltered().isPresent();
  }
}
