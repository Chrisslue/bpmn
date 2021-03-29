package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.ListMultimap;

import java.util.Collection;
import java.util.List;

public class ASTFlowBranch extends ASTFlowBranchTOP {

    protected ASTFlowBranch() {
        super();
    }

    protected ASTFlowBranch(final List<ASTFlowTarget> path) {
        super(path);
    }

    public ListMultimap<ASTFlowNode, List<ASTFlowCondition>> asTarget() {
        return getPath(0).asTarget();
    }

    public Collection<ASTFlowNode> asSource() {
        return getPath(sizePaths() - 1).asSource();
    }

    public boolean isDefault() {
        return !isEmptyPaths() && getPathList().get(0).getConditionOpt().map(ASTFlowCondition::isDefault).orElse(false);
    }

}
