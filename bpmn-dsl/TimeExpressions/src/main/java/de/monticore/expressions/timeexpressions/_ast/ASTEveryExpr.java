package de.monticore.expressions.timeexpressions._ast;

import com.google.common.base.Joiner;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Optional;

public class ASTEveryExpr extends ASTEveryExprTOP {

    protected ASTEveryExpr() {
        super();
    }

    protected ASTEveryExpr(final Optional<ASTDateExpr> start, final Optional<ASTIntLiteral> times, final ASTPeriod period) {
        super(start, times, period);
    }

    public Optional<LocalDate> getStartLocalDate() {
        return getStartOpt().map(ASTDateExpr::getLocalDate);
    }

    public Optional<LocalDateTime> getStartLocalDateTime() {
        return getStartOpt().flatMap(ASTDateExpr::getLocalDateTime);
    }

    public String printISO8601() {
        return Joiner.on("/").skipNulls().join(
                "R" + times.map(ASTIntLiteral::getValue).map(String::valueOf).orElse(""),
                getStartOpt().map(ASTDateExpr::printISO8601).orElse(null),
                getPeriod().printISO8601()
        );
    }

}
