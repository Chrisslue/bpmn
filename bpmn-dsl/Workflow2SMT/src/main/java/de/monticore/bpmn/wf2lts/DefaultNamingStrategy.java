package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.workflow._ast.ASTFlowNode;

public class DefaultNamingStrategy implements NamingStrategy<ASTFlowNode> {

  @Override
  public String apply(ASTFlowNode node) {
    return node.getName();
  }
}
