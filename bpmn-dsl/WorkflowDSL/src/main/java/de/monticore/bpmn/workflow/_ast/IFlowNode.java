package de.monticore.bpmn.workflow._ast;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface IFlowNode {

    String getName();

    List<SequenceFlow> getIncomingList() ;

    List<SequenceFlow> getOutgoingList() ;

    default Set<ASTFlowNode> getSuccessors() {
        return getOutgoingList().stream().map(SequenceFlow::getTarget).collect(Collectors.toSet());
    }

    default Set<ASTFlowNode> getPredecessors() {
        return getIncomingList().stream().map(SequenceFlow::getSource).collect(Collectors.toSet());
    }

}
