package de.monticore.bpmn.utils;

import de.monticore.bpmn.collectors.WorkflowFilter;
import de.monticore.bpmn.workflow._ast.*;

import java.util.Optional;
import java.util.stream.Stream;


/**
 * Utilities to avoid casting in streams
 *
 * Usage: flowNodes.stream().flatMap(WorkflowFilters::isTask).forEach(...)
  */
public class WorkflowFilters {

    public static Stream<ASTTask> isTask(final ASTWorkflowNode node) {
        return Stream.of(new WorkflowFilter<ASTTask>(node) {
                @Override
                public void visit(ASTTask node) {
                    select(node);
                }
            }.getFiltered())
                .filter(Optional::isPresent).map(Optional::get);
    }

    public static Stream<ASTEvent> isEvent(final ASTWorkflowNode node) {
        return Stream.of(new WorkflowFilter<ASTEvent>(node) {
            @Override
            public void visit(ASTEvent node) {
                select(node);
            }
        }.getFiltered())
                .filter(Optional::isPresent).map(Optional::get);
    }

    public static Stream<ASTGateway> isGateway(final ASTWorkflowNode node) {
        return Stream.of(new WorkflowFilter<ASTGateway>(node) {
            @Override
            public void visit(ASTGateway node) {
                select(node);
            }
        }.getFiltered())
                .filter(Optional::isPresent).map(Optional::get);
    }

    public static Stream<ASTEventTriggerMessage> isMessageTrigger(final ASTWorkflowNode node) {
        return Stream.of(new WorkflowFilter<ASTEventTriggerMessage>(node) {
            @Override
            public void visit(ASTEventTriggerMessage node) {
                select(node);
            }
        }.getFiltered())
                .filter(Optional::isPresent).map(Optional::get);
    }

    public static Optional<ASTEventTriggerMessage> getMessageTrigger(final ASTWorkflowNode node) {
        return new WorkflowFilter<ASTEventTriggerMessage>(node) {
            @Override
            public void handle(ASTEventTriggerMessage node) {
                select(node);
            }
        }.getFiltered();
    }

    public static boolean isCompensateTrigger(final ASTWorkflowNode node) {
        return new WorkflowFilter<ASTEventTriggerCompensate>(node) {
            @Override
            public void handle(ASTEventTriggerCompensate node) {
                select(node);
            }
        }.getFiltered().isPresent();
    }

}
