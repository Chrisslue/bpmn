package de.monticore.bpmn.workflow._ast;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IFlowNode {

  String getName();

  List<SequenceFlow> getIncomingsList();

  List<SequenceFlow> getOutgoingsList();

  default Set<ASTFlowNode> getSuccessors() {
    return getOutgoingsList().stream().map(SequenceFlow::getTarget).collect(Collectors.toSet());
  }

  default Set<ASTFlowNode> getPredecessors() {
    return getIncomingsList().stream().map(SequenceFlow::getSource).collect(Collectors.toSet());
  }
}
