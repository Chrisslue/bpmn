package de.monticore.wf2lts;

import de.monticore.bpmn.workflow._ast.IFlowNode;

public class DefaultNamingStrategy implements NamingStrategy<IFlowNode> {

  @Override
  public String apply(IFlowNode node) {
    return node.getName();
  }
}
