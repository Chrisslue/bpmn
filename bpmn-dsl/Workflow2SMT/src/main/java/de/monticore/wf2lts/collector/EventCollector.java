package de.monticore.wf2lts.collector;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

import java.util.ArrayList;
import java.util.List;

/*
 * Collect all NamedEvents and InlineEvents (only occur in sequence-flows).
 */
public class EventCollector implements WorkflowHandler {

  private final List<ASTEvent> events = new ArrayList<>();

  private WorkflowTraverser traverser;

  protected EventCollector(WorkflowTraverser traverser) {
    traverser.setWorkflowHandler(this);
  }

  @Override
  public WorkflowTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(WorkflowTraverser traverser) {
    this.traverser = traverser;
  }

  public List<ASTEvent> getEvents() {
    return events;
  }

  protected void visitFlow(ASTSequenceFlow sequenceFlow) {
    for (ASTFlowTarget flowTarget : sequenceFlow.getPathList()) {
      // If the flow target is an event we want to handle it.
      if (flowTarget.isPresentNode()) {
        flowTarget.getNode().accept(traverser); // This is an inline event.
      }
      else if (flowTarget.isPresentNodeRef()) {
        // We have to resolve the reference.
        var optReferencedNode = flowTarget.getEnclosingScope()
            .resolveNamedEvent(flowTarget.getNodeRef().getBaseName());
        optReferencedNode.ifPresent(namedEvent -> namedEvent.accept(traverser));
      }
    }
  }

  @Override
  public void handle(ASTSequenceFlow sequenceFlow) {
    this.visitFlow(sequenceFlow);
  }

  /**
   * Override all handle methods in order to prevent further traversing of the ast.
   */

  @Override
  public void handle(ASTConditionExpression node) {
  }

  @Override
  public void handle(ASTProcess node) {
  }

  @Override
  public void handle(ASTSubProcess node) {
  }

  @Override
  public void handle(ASTTask node) {
  }

  @Override
  public void handle(ASTNamedGateway node) {
  }

  @Override
  public void handle(ASTInlineGateway node) {
  }

}


