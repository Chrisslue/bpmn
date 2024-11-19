package de.monticore.timer.timerconditions._ast;

public class ASTAfterPeriodCondition extends ASTAfterPeriodConditionTOP {

  public String printISO8601() {
    return getPeriod().printISO8601();
  }
}
