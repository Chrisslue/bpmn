package de.monticore.wf2lts;

import de.monticore.bpmn.workflow._ast.IFlowNode;

import java.util.function.Function;

public interface NamingStrategy extends Function<IFlowNode, String> {

  @Override
  default String apply(IFlowNode node) {
    return node.getName();
  }

}
