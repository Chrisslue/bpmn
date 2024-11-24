package de.monticore.workflow.conformance.datastructure;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.workflow.conformance.datastructure.interf.IDWfNodeBuilder;
import de.monticore.workflow.conformance.utils.NodeType;

/***
 * This class take as parameter a workflowBuilder.  It visits workflow element
 * and use the builder to transform workflow elements (tasks, events, gateways, etc.)
 * in to Node. The builder also collects the sequence flows.
 */
public class WfElementFactory implements WorkflowVisitor2 {

  private final IDWfNodeBuilder builder;

  public WfElementFactory(IDWfNodeBuilder builder) {
    this.builder = builder;
  }

  @Override
  public void visit(ASTNamedEvent node) {
    if (node.isStart()) {
      builder.mkStartEvent(node.getName());
    } else if (node.isEnd()) {
      builder.mkEndEvent(node.getName());
    } else {
      builder.mkNamedEvent(node.getName());
    }
  }

  @Override
  public void visit(ASTTask node) {
    builder.mkNamedTask(node.getName());
  }

  @Override
  public void visit(ASTSequenceFlow node) {
    builder.addSequenceFlow(node);
  }

  @Override
  public void visit(ASTNamedGateway node) {
    builder.mkNamedGateway(node.getName(), getGatewayType(node));
  }

  protected NodeType getGatewayType(ASTGateway gateway) {
    boolean isMerge = gateway.getDirection().name().equals("MERGE");

    if (gateway.getType().isExclusive()) {
      return isMerge ? NodeType.XOR_MERGE : NodeType.XOR_SPLIT;
    } else if (gateway.getType().isInclusive()) {
      return isMerge ? NodeType.OR_MERGE : NodeType.OR_SPLIT;
    } else {
      return isMerge ? NodeType.AND_MERGE : NodeType.AND_SPLIT;
    }
  }
}
