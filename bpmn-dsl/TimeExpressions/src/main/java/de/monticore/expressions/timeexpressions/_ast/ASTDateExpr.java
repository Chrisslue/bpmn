package de.monticore.expressions.timeexpressions._ast;

import java.util.Optional;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ASTDateExpr extends ASTDateExprTOP {

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
    return timeExpr.map(
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
        timeExpr.map(ASTTimeExpr::getHours).orElse(0),
        timeExpr.map(ASTTimeExpr::getMinutes).orElse(0),
        timeExpr.map(ASTTimeExpr::getSeconds).orElse(0));
  }

  public String printISO8601() {
    return getLocalDateTime()
        .map(LocalDateTime::toString)
        .orElseGet(() -> getLocalDate().toString());
  }
}
