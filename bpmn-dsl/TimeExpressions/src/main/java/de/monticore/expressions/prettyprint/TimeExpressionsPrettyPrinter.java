package de.monticore.expressions.prettyprint;

import de.monticore.expressions.timeexpressions._visitor.TimeExpressionsDelegatorVisitor;
import de.monticore.expressions.visitors.TimeExpressionsPrettyConcretePrinterVisitor;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty printer for time expressions.
 */
public class TimeExpressionsPrettyPrinter extends TimeExpressionsDelegatorVisitor {

    private final IndentPrinter out;

    public TimeExpressionsPrettyPrinter(final IndentPrinter printer) {
        this.out = printer;

        setTimeExpressionsVisitor(new TimeExpressionsPrettyConcretePrinterVisitor(printer));
    }

}
