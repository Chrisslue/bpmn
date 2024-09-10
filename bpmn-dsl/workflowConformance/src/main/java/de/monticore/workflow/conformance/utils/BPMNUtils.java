package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.wf2lts.collector.StartEventCollector;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

public class BPMNUtils {
  public static ASTEvent getStartEvent(ASTWorkflowCompilationUnit ast) {

    WorkflowTraverser traverser = WorkflowMill.traverser();
    StartEventCollector startEventCollector = new StartEventCollector(traverser);
    for (ASTFlowElement astFlowElement : ast.getProcess().getFlowElementList()) {
      astFlowElement.accept(traverser);
    }
    var startEvents = startEventCollector.getEvents();
    if (startEvents.size() != 1) {
      throw new IllegalArgumentException("Workflow had more than one start element");
    }
    return startEvents.get(0);
  }
}
