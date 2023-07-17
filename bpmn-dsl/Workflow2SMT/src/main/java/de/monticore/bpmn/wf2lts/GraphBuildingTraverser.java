package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.wf2lts.datastructure.EdgeTo;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.wf2lts.scopes.SubProcessScope;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTInlineGateway;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphBuildingTraverser implements WorkflowHandler, WorkflowVisitor2 {

  protected final IntermediateGraphWithScopes graph;

  protected WorkflowTraverser traverser;

  protected Set<ASTFlowNode> visited;

  public GraphBuildingTraverser(WorkflowTraverser traverser, ASTFlowNode startElement) {
    this(traverser, new IntermediateGraphWithScopes(startElement));
  }

  public GraphBuildingTraverser(WorkflowTraverser traverser, IntermediateGraphWithScopes graph) {
    traverser.setWorkflowHandler(this);
    traverser.getWorkflowVisitorList().add(this);
    this.graph = graph;
    this.visited = new HashSet<>();
  }

  public static IntermediateGraphWithScopes graphOf(ASTEvent startEvent) {
    if (!startEvent.isStart()) {
      throw new IllegalArgumentException("startEvent has to be a start event");
    }
    GraphBuildingTraverser handler =
        new GraphBuildingTraverser(WorkflowMill.traverser(), startEvent);
    startEvent.accept(handler.getTraverser());
    return handler.getGraph();
  }

  public IntermediateGraphWithScopes getGraph() {
    return graph;
  }

  protected void addEdge(ASTFlowNode source, ASTFlowNode target) {
    addEdge(source, target, Collections.emptyList());
  }

  protected void addEdge(
      ASTFlowNode source, ASTFlowNode target, List<ASTFlowCondition> conditions) {
    var targetNodes = getGraph().getEdges().getOrDefault(source, new ArrayList<>());
    targetNodes.add(new EdgeTo<>(conditions, target));
    getGraph().getEdges().putIfAbsent(source, targetNodes);
  }

  protected void addOutgoingsAsEdges(ASTFlowNode node) {
    this.addOutgoingsAsEdges(node, node.getOutgoingsList());
  }

  protected void addOutgoingsAsEdges(ASTFlowNode nameOfNode, List<SequenceFlow> outgoings) {
    for (SequenceFlow sequenceFlow : outgoings) {
      addEdge(nameOfNode, sequenceFlow.getTarget(), sequenceFlow.getConditions());
    }
  }

  @Override
  public WorkflowTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(WorkflowTraverser traverser) {
    this.traverser = traverser;
  }

  /**
   * Enclose everything between two matching gateways (split -> ... -> merge) as one GatewayScope.
   * This enables easy implementations of different interleaving strategies. The GatewayScope itself
   * will build in internal graph of the scope between the gateways.
   */
  private void addAndHandleGatewayScope(ASTGateway gateway) {
    ASTFlowNode continueFrom;
    if (gateway.isDiverging()) {
      GatewayScope gatewayScope = new GatewayScope(WorkflowMill.traverser(), gateway);
      getGraph().getGatewayScopes().add(gatewayScope);
      if (gatewayScope.getClosingGateway().isEmpty()) {
        return;
      }
      continueFrom = gatewayScope.getClosingGateway().get();
      addOutgoingsAsEdges(continueFrom);
    } else {
      continueFrom = gateway;
    }
    traverseOutgoingTargets(continueFrom);
  }

  @Override
  public void handle(ASTNamedGateway node) {
    addAndHandleGatewayScope(node);
  }

  @Override
  public void handle(ASTInlineGateway node) {
    addAndHandleGatewayScope(node);
  }

  /*
   * Whenever a node is visited add the outgoing transitions as edges to the graph.
   */

  @Override
  public void visit(ASTSubProcess node) {
    addOutgoingsAsEdges(node);
    SubProcessScope subProcessScope = new SubProcessScope(node);
    getGraph().getSubProcessScopes().add(subProcessScope);
  }

  @Override
  public void visit(ASTTask node) {
    addOutgoingsAsEdges(node);
  }

  @Override
  public void visit(ASTNamedEvent node) {
    addOutgoingsAsEdges(node);
  }

  @Override
  public void visit(ASTInlineEvent node) {
    addOutgoingsAsEdges(node);
  }

  /*
   * Traverse through the diagram by moving along the outgoing SequenceFlows.
   */

  protected final void traverseOutgoingTargets(ASTFlowNode node) {
    for (SequenceFlow sequenceFlow : node.getOutgoingsList()) {
      if (!visited.contains(sequenceFlow.getTarget())) {
        visited.add(sequenceFlow.getTarget());
        sequenceFlow.getTarget().accept(getTraverser());
      }
    }
  }

  @Override
  public void traverse(ASTSubProcess node) {
    traverseOutgoingTargets(node);
  }

  @Override
  public void traverse(ASTTask node) {
    traverseOutgoingTargets(node);
  }

  @Override
  public void traverse(ASTInlineGateway node) {
    traverseOutgoingTargets(node);
  }

  @Override
  public void traverse(ASTNamedEvent node) {
    traverseOutgoingTargets(node);
  }

  @Override
  public void traverse(ASTInlineEvent node) {
    traverseOutgoingTargets(node);
  }
}
