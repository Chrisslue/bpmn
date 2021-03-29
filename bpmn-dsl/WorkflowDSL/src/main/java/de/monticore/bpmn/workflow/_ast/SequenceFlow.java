package de.monticore.bpmn.workflow._ast;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SequenceFlow {

    private final ASTFlowNode source;
    private final ASTFlowNode target;
    private final List<ASTFlowCondition> conditions;

    SequenceFlow(
            final ASTFlowNode source,
            final ASTFlowNode target,
            final List<ASTFlowCondition> conditions
    ) {
        this.source = checkNotNull(source);
        this.target = checkNotNull(target);
        this.conditions = conditions;
    }

    public ASTFlowNode getSource() {
        return source;
    }

    public ASTFlowNode getTarget() {
        return target;
    }

    public String getSourceName() {
        return getSource().getName();
    }

    public String getTargetName() {
        return getTarget().getName();
    }

    public List<ASTFlowCondition> getConditions() {
        return conditions;
    }

    public boolean isConditional() {
        return conditions.stream().anyMatch(ASTFlowCondition::isPresentCondition);
    }

    public boolean isDefault() {
        return !conditions.isEmpty() && conditions.stream().allMatch(ASTFlowCondition::isDefault);
    }

    public String getName() {
        return  "From" + getSourceName() + "To" + getTargetName();
    }

}
