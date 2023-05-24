package de.monticore.expressions.timeexpressions._ast;

import com.google.common.base.Joiner;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import java.util.Optional;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ASTEveryExpr extends ASTEveryExprTOP {

  public Optional<LocalDate> getStartLocalDate() {
    return start.map(ASTDateExpr::getLocalDate);
  }

  public Optional<LocalDateTime> getStartLocalDateTime() {
    return start.flatMap(ASTDateExpr::getLocalDateTime);
  }

  public String printISO8601() {
    return Joiner.on("/")
        .skipNulls()
        .join(
            "R" + times.map(ASTNatLiteral::getValue).map(String::valueOf).orElse(""),
            start.map(ASTDateExpr::printISO8601).orElse(null),
            getPeriod().printISO8601());
  }
}
