package de.monticore.bpmn.timerconditions._ast;

import de.monticore.bpmn.timerconditions._ast.ASTAfterPeriodConditionTOP;

public class ASTAfterPeriodCondition extends ASTAfterPeriodConditionTOP {

  public String printISO8601() {
    return getPeriod().printISO8601();
  }
}
