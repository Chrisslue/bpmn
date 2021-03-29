package de.monticore.expressions.timeexpressions._ast;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import org.joda.time.LocalTime;

import java.util.Optional;

public class ASTTime extends ASTTimeTOP {

    protected ASTTime() {
        super();
    }

    protected ASTTime(final ASTIntLiteral hours, final ASTIntLiteral minutes, final Optional<ASTIntLiteral> seconds) {
        super(hours, minutes, seconds);
    }

    public LocalTime getLocalTime() {
        return new LocalTime(hours.getValue(), minutes.getValue(), seconds.map(ASTIntLiteral::getValue).orElse(0));
    }

}
