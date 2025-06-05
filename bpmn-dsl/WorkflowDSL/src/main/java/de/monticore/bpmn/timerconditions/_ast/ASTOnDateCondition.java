package de.monticore.bpmn.timerconditions._ast;

import java.util.Optional;

import de.monticore.bpmn.timerconditions._ast.ASTOnDateConditionTOP;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ASTOnDateCondition extends ASTOnDateConditionTOP {

  public int getYear() {
    return getDate().getYear().getValue();
  }

  public int getMonth() {
    return getDate().getMonth().getValue();
  }

  public int getDay() {
    return getDate().getDay().getValue();
  }

  public LocalDate getLocalDate() {
    return getDate().getLocalDate();
  }

  public Optional<LocalDateTime> getLocalDateTime() {
    return atTimeCondition.map(
        time ->
            new LocalDateTime(
                getYear(),
                getMonth(),
                getDay(),
                time.getHours(),
                time.getMinutes(),
                time.getSeconds()));
  }

  public LocalDateTime getLocalDateTimeOrDefaultMidnight() {
    return new LocalDateTime(
        getYear(),
        getMonth(),
        getDay(),
        atTimeCondition.map(ASTAtTimeCondition::getHours).orElse(0),
        atTimeCondition.map(ASTAtTimeCondition::getMinutes).orElse(0),
        atTimeCondition.map(ASTAtTimeCondition::getSeconds).orElse(0));
  }

  public String printISO8601() {
    return getLocalDateTime()
        .map(LocalDateTime::toString)
        .orElseGet(() -> getLocalDate().toString());
  }
}
