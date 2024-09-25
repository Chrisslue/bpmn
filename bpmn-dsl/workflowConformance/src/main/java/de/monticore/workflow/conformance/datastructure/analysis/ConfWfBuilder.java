package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;

import java.util.List;
import java.util.Optional;

public class ConfWfBuilder implements WfBuilder<ConfWfNode> {
    @Override
    public NodeBuilder<ConfWfNode> mkNamedTask(String name) {
        return new  ConfNodeBuilder(name,NodeType.TASK);
    }

    @Override
    public NodeBuilder<ConfWfNode> mkNamedEvent(String name) {
        return new  ConfNodeBuilder(name,NodeType.TASK); //todo change to event
    }

    @Override
    public NodeBuilder<ConfWfNode> mkNamedGateway(String name, NodeType type) {
        return new ConfNodeBuilder(name,type);
    }

    @Override
    public NodeBuilder<ConfWfNode> mkXor(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> mkSequence(List<NodeBuilder<ConfWfNode>> nodes) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> mkAnd(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> mkOr(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> mkLoop(String name, NodeBuilder<ConfWfNode> forward, NodeBuilder<ConfWfNode> backward) {
        return null;
    }





    protected NodeType resolveGatewayType(ASTGateway gateway) {
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
