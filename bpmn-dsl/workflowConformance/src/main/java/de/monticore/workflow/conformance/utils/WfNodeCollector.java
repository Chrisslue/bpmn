package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.HashSet;
import java.util.Set;

public class WfNodeCollector implements WorkflowVisitor2 {

  private final Set<ASTNamedEvent> namedEvents = new HashSet<>();
  private final Set<ASTTask> tasks = new HashSet<>();
  private final Set<ASTGateway> gateways = new HashSet<>();
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private ASTNamedEvent startEvent;

  @Override
  public void visit(ASTNamedEvent node) {
    namedEvents.add(node);
    if (node.isStart()) {
      this.startEvent = node;
    }
  }

  @Override
  public void visit(ASTTask node) {
    tasks.add(node);
  }

  @Override
  public void visit(ASTSequenceFlow node) {
    sequenceFlows.add(node);
  }

  @Override
  public void visit(ASTNamedGateway node) {
    gateways.add(node);
  }

  public Set<ASTFlowElement> getAllFlowElements() {
    Set<ASTFlowElement> res = new HashSet<>();

    res.addAll(namedEvents);
    res.addAll(tasks);
    res.addAll(gateways);

    return res;
  }

  public Set<ASTGateway> getGateways() {
    return gateways;
  }

  public Set<ASTNamedEvent> getNamedEvents() {
    return namedEvents;
  }

  public Set<ASTTask> getTasks() {
    return tasks;
  }

  public Set<ASTSequenceFlow> getSequenceFlows() {
    return sequenceFlows;
  }

  public ASTNamedEvent getStartEvent() {
    return startEvent;
  }
}
