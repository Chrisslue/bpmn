/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions._ast;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import org.joda.time.LocalTime;

public class ASTAtTimeCondition extends ASTAtTimeConditionTOP {
  
  public int getHours() { return getTime().getHours().getValue(); }
  
  public int getMinutes() { return getTime().getMinutes().getValue(); }
  
  public int getSeconds() { return getTime().seconds.map(ASTNatLiteral::getValue).orElse(0); }
  
  public LocalTime getLocalTime() { return time.getLocalTime(); }
  
  public String printISO8601() {
    return getLocalTime().toString();
  }
  
}
