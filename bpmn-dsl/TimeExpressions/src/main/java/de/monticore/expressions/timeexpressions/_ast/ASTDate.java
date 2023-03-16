package de.monticore.expressions.timeexpressions._ast;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import org.joda.time.LocalDate;

public class ASTDate extends ASTDateTOP {

    public LocalDate getLocalDate() {
        return new LocalDate(year.getValue(), month.getValue(), day.getValue());
    }

}
