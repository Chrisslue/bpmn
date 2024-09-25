package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

public class ConfWfNode implements WfNode {
    @Override
    public NodeType getNodeType() {
        return null;
    }

    @Override
    public String getLabel() {
        return "";
    }

    @Override
    public Optional<WfNode> existsPredecessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
        return Optional.empty();
    }

    @Override
    public Set<WfNode> allPredecessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
        return Set.of();
    }
}
