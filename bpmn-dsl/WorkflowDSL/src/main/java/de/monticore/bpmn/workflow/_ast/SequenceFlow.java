package de.monticore.bpmn.workflow._ast;

import static com.google.common.base.Preconditions.checkNotNull;

import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import java.util.List;

public class SequenceFlow {

  private final ASTFlowNode source;
  private final ASTFlowNode target;
  private final List<ASTFlowCondition> conditions;

  SequenceFlow(
      final ASTFlowNode source, final ASTFlowNode target, final List<ASTFlowCondition> conditions) {
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
    return "From" + getSourceName() + "To" + getTargetName();
  }

  public SequenceFlow deepClone() {
    return new SequenceFlow(this.getSource(), this.getTarget(), this.getConditions());
  }

  public boolean deepEquals(SequenceFlow flow) {
    if (flow.getConditions().size() != getConditions().size()) {
      return false;
    }
    for (int i = 0; i < getConditions().size(); i++) {
      if (!flow.getConditions().get(i).deepEquals(getConditions().get(i))) {
        return false;
      }
    }
    return flow.getSource().deepEquals(getSource()) && flow.getTarget().deepEquals(getTarget());
  }

  public boolean deepEquals(SequenceFlow flow, boolean forceSameOrder) {
    return deepEquals(flow);
  }

  public boolean deepEqualsWithComments(SequenceFlow flow) {
    if (flow.getConditions().size() != getConditions().size()) {
      return false;
    }
    for (int i = 0; i < getConditions().size(); i++) {
      if (!flow.getConditions().get(i).deepEqualsWithComments(getConditions().get(i))) {
        return false;
      }
    }
    return flow.getSource().deepEqualsWithComments(getSource())
        && flow.getTarget().deepEqualsWithComments(getTarget());
  }

  public boolean deepEqualsWithComments(SequenceFlow flow, boolean forceSameOrder) {
    return deepEqualsWithComments(flow);
  }

  public void accept(WorkflowTraverser traverser) {}
}
