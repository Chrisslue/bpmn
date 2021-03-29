package de.monticore.expressions.timeexpressions._ast;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Optional;

public class ASTDateExpr extends ASTDateExprTOP {

    protected ASTDateExpr() {
        super();
    }

    protected ASTDateExpr(final ASTDate date, final Optional<ASTTimeExpr> time) {
        super(date, time);
    }

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
        return getTimeExprOpt().map(time -> new LocalDateTime(
                getYear(),
                getMonth(),
                getDay(),
                time.getHours(),
                time.getMinutes(),
                time.getSeconds()
        ));
    }

    public LocalDateTime getLocalDateTimeOrDefaultMidnight() {
        return new LocalDateTime(
                getYear(),
                getMonth(),
                getDay(),
                getTimeExprOpt().map(ASTTimeExpr::getHours).orElse(0),
                getTimeExprOpt().map(ASTTimeExpr::getMinutes).orElse(0),
                getTimeExprOpt().map(ASTTimeExpr::getSeconds).orElse(0)
        );
    }

    public String printISO8601() {
        return getLocalDateTime().map(LocalDateTime::toString).orElseGet(() -> getLocalDate().toString());
    }

}
