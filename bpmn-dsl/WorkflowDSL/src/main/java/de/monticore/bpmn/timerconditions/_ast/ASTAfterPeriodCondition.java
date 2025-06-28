/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions._ast;

public class ASTAfterPeriodCondition extends ASTAfterPeriodConditionTOP {
  
  public String printISO8601() {
    return getPeriod().printISO8601();
  }
  
}
