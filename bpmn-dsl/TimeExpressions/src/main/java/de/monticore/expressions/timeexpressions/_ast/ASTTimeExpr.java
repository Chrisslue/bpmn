package de.monticore.expressions.timeexpressions._ast;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import org.joda.time.LocalTime;

public class ASTTimeExpr extends ASTTimeExprTOP {

    protected ASTTimeExpr() {
        super();
    }

    protected ASTTimeExpr(final ASTTime time) {
        super(time);
    }

    public int getHours() {
        return getTime().getHours().getValue();
    }

    public int getMinutes() {
        return getTime().getMinutes().getValue();
    }

    public int getSeconds() {
        return getTime().getSecondsOpt().map(ASTIntLiteral::getValue).orElse(0);
    }

    public LocalTime getLocalTime() {
        return time.getLocalTime();
    }

    public String printISO8601() {
        return getLocalTime().toString();
    }

}
