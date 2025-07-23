/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions._ast;

import com.google.common.base.Joiner;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ASTEveryTimeCondition extends ASTEveryTimeConditionTOP {
  
  public Optional<LocalDate> getStartLocalDate() {
    return start.map(ASTOnDateCondition::getLocalDate);
  }
  
  public Optional<LocalDateTime> getStartLocalDateTime() {
    return start.flatMap(ASTOnDateCondition::getLocalDateTime);
  }
  
  public String printISO8601() {
    return Joiner.on("/").skipNulls().join("R" + times.map(ASTNatLiteral::getValue).map(
        String::valueOf).orElse(""), start.map(ASTOnDateCondition::printISO8601).orElse(null),
        getPeriod().printISO8601());
  }
  
}
