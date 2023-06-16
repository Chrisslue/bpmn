package de.monticore.wf2lts.datastructure;

import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.wf2lts.scopes.GatewayScope;
import de.monticore.wf2lts.scopes.SubProcessScope;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntermediateGraphWithScopes
    extends IntermediateGraph<ASTFlowNode, EdgeTo<ASTFlowNode>> {

  private final List<SubProcessScope> subProcessScopes;

  private final List<GatewayScope> gatewayScopes;

  public IntermediateGraphWithScopes(ASTFlowNode startNode) {
    this(startNode, new HashMap<>());
  }

  public IntermediateGraphWithScopes(
      ASTFlowNode startNode, Map<ASTFlowNode, List<EdgeTo<ASTFlowNode>>> edges) {
    super(startNode, edges);
    this.gatewayScopes = new ArrayList<>();
    this.subProcessScopes = new ArrayList<>();
  }

  @Override
  public Map<ASTFlowNode, List<EdgeTo<ASTFlowNode>>> getEdges() {
    return super.getEdges();
  }

  public List<SubProcessScope> getSubProcessScopes() {
    return subProcessScopes;
  }

  public List<GatewayScope> getGatewayScopes() {
    return gatewayScopes;
  }

  @Override
  public List<ASTFlowNode> predecessorNodes(ASTFlowNode node) {
    return super.predecessorNodes(node);
  }

  @Override
  public Map<ASTFlowNode, List<EdgeTo<ASTFlowNode>>> predecessors(ASTFlowNode node) {
    return super.predecessors(node);
  }

  @Override
  public List<ASTFlowNode> getSources(EdgeTo<ASTFlowNode> edge) {
    return super.getSources(edge);
  }
}
