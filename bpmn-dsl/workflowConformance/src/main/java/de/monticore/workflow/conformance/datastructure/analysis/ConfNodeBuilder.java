package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;

public class ConfNodeBuilder implements NodeBuilder<ConfWfNode> {

    private final String label;

    private  final NodeType nodeType ;

    public ConfNodeBuilder(String label, NodeType nodeType){
        this.label = label ;
        this.nodeType = nodeType;
    }
    @Override
    public NodeBuilder<ConfWfNode> addPredecessor(NodeBuilder<ConfWfNode>... t) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> addPredecessor(NodeBuilder<ConfWfNode> t) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> addSuccessor(NodeBuilder<ConfWfNode>... t) {
        return null;
    }

    @Override
    public NodeBuilder<ConfWfNode> addSuccessor(NodeBuilder<ConfWfNode> t) {
        return null;
    }
}
