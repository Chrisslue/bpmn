package de.monticore.wf2ltl.datastructure;

import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.wf2ltl.NamingStrategy;
import de.monticore.wf2ltl.scopes.GatewayScope;
import de.monticore.wf2ltl.scopes.SubProcessScope;
import de.monticore.wf2ltl.transformer.GatewayTransformer;
import de.monticore.wf2ltl.transformer.SubprocessTransformer;

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

  public IntermediateGraphWithScopes(ASTFlowNode startNode,
      Map<ASTFlowNode, List<EdgeTo<ASTFlowNode>>> edges) {
    super(startNode, edges);
    this.gatewayScopes = new ArrayList<>();
    this.subProcessScopes = new ArrayList<>();
  }

  /**
   * TODO this should probably be a strategy too.
   * Convert from a state based graph to a labeled-transition based LTS.
   * Push all node-label to all incoming edges.
   */
  public LTS asLTS(NamingStrategy namingStrategy, SubprocessTransformer subprocessTransformer,
      GatewayTransformer gatewayTransformer) {
    throw new UnsupportedOperationException("TODO implement this method");
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
