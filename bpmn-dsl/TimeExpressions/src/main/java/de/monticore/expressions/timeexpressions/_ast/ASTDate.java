package de.monticore.expressions.timeexpressions._ast;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import org.joda.time.LocalDate;

public class ASTDate extends ASTDateTOP {

    protected ASTDate() {
        super();
    }

    protected ASTDate(ASTIntLiteral year, ASTIntLiteral month, ASTIntLiteral day) {
        super(year, month, day);
    }

    public LocalDate getLocalDate() {
        return new LocalDate(year.getValue(), month.getValue(), day.getValue());
    }

}
