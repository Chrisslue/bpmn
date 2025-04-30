package de.monticore.bpmn.timerconditions._ast;

import de.monticore.bpmn.timerconditions._ast.ASTDateTOP;
import org.joda.time.LocalDate;

public class ASTDate extends ASTDateTOP {

  public LocalDate getLocalDate() {
    return new LocalDate(year.getValue(), month.getValue(), day.getValue());
  }
}
