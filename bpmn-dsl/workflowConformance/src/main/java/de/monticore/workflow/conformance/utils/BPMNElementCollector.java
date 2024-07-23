package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.*;

public class BPMNElementCollector implements WorkflowVisitor2 {

  private Map<String, ASTNamedEvent> namedEvents = new HashMap<>();
  private Map<String, ASTTask> tasks = new HashMap<>();
  private Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private Map<String, ASTGateway> gateways = new HashMap<>();

  @Override
  public void visit(ASTNamedEvent node) {
    namedEvents.put(node.getName(), node);
  }

  @Override
  public void visit(ASTTask node) {
    tasks.put(node.getName(), node);
  }

  @Override
  public void visit(ASTSequenceFlow node) {
    sequenceFlows.add(node);
  }

  @Override
  public void visit(ASTNamedGateway node) {
    gateways.put(node.getName(), node);
  }

  public Map<String, ASTFlowElement> getAllFlowElements() {
    Map<String, ASTFlowElement> res = new HashMap<>();

    res.putAll(namedEvents);
    res.putAll(tasks);
    res.putAll(gateways);

    return res;
  }

  public ASTFlowElement getFlowElement(String name) {
    return getAllFlowElements().get(name);
  }

  public Set<ASTSequenceFlow> getSequenceFlows() {
    return sequenceFlows;
  }
}
