package de.monticore.wf2lts.datastructure;

import de.monticore.bpmn.workflow._ast.ASTFlowCondition;

import java.util.List;

public class EdgeTo<S> {

  private final List<ASTFlowCondition> conditions;

  private final S target;

  public EdgeTo(List<ASTFlowCondition> conditions, S target) {
    this.conditions = conditions;
    this.target = target;
  }

  public List<ASTFlowCondition> getConditions() {
    return conditions;
  }

  public S getTarget() {
    return target;
  }

}
