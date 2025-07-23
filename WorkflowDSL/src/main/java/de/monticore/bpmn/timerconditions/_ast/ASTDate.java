/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions._ast;

import org.joda.time.LocalDate;

public class ASTDate extends ASTDateTOP {
  
  public LocalDate getLocalDate() {
    return new LocalDate(year.getValue(), month.getValue(), day.getValue());
  }
  
}
