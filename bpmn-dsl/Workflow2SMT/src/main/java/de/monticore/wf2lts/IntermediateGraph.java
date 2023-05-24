package de.monticore.wf2lts;

import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import java.util.*;

public class IntermediateGraph {

  private final ASTFlowNode startNode;

  private final Map<ASTFlowNode, List<EdgeTo>> edges;

  private final List<SubProcessScope> subProcessScopes;

  private final List<GatewayScope> gatewayScopes;

  public IntermediateGraph(ASTFlowNode startNode) {
    this(startNode, new HashMap<>());
  }

  public IntermediateGraph(ASTFlowNode startNode, Map<ASTFlowNode, List<EdgeTo>> edges) {
    this.startNode = startNode;
    this.edges = edges;
    this.gatewayScopes = new ArrayList<>();
    this.subProcessScopes = new ArrayList<>();
  }

  public ASTFlowNode getStartNode() {
    return startNode;
  }

  public Map<ASTFlowNode, List<EdgeTo>> getEdges() {
    return edges;
  }

  public List<SubProcessScope> getSubProcessScopes() {
    return subProcessScopes;
  }

  public List<GatewayScope> getGatewayScopes() {
    return gatewayScopes;
  }

  @Override
  public String toString() {
    return "IntermediateGraph{" + "startNode=" + startNode.getName() + ", edges=" + edges + '}';
  }

  static class EdgeTo {
    private final ASTFlowNode target;

    private final List<ASTFlowCondition> conditions;

    public EdgeTo(ASTFlowNode target) {
      this(target, Collections.emptyList());
    }

    public EdgeTo(ASTFlowNode target, List<ASTFlowCondition> conditions) {
      this.target = target;
      this.conditions = conditions;
    }

    public ASTFlowNode getTarget() {
      return target;
    }

    public List<ASTFlowCondition> getConditions() {
      return conditions;
    }

    @Override
    public String toString() {
      return "EdgeTo{" + "target=" + target.getName() + ", conditions=" + conditions + '}';
    }
  }
}
