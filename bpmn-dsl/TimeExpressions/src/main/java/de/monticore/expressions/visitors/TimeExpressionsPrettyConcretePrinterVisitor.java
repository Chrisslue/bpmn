package de.monticore.expressions.visitors;

import de.monticore.expressions.timeexpressions._ast.*;
import de.monticore.expressions.timeexpressions._visitor.TimeExpressionsVisitor;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty printer visitor for time expressions.
 */
public class TimeExpressionsPrettyConcretePrinterVisitor implements TimeExpressionsVisitor {

    private final IndentPrinter out;
    private TimeExpressionsVisitor realThis;

    public TimeExpressionsPrettyConcretePrinterVisitor(IndentPrinter printer) {
        realThis = this;
        out = printer;
    }

    @Override
    public TimeExpressionsVisitor getRealThis() {
        return realThis;
    }

    @Override
    public void setRealThis(TimeExpressionsVisitor realThis) {
        this.realThis = realThis;
    }

    @Override
    public void handle(final ASTTimeExpr node) {
        out.print("at ");
        node.getTime().accept(getRealThis());
    }

    @Override
    public void handle(final ASTDateExpr node) {
        out.print("on ");
        node.getDate().accept(getRealThis());
        if (node.isPresentTimeExpr()) {
            out.print(" ");
            node.getTimeExpr().accept(getRealThis());
        }
    }

    @Override
    public void handle(final ASTAfterExpr node) {
        out.print("after ");
        node.getPeriod().accept(getRealThis());
    }

    @Override
    public void handle(final ASTEveryExpr node) {
        if (node.isPresentStart()) {
            out.print("start ");
            node.getStart().accept(getRealThis());
            out.print(", ");
        }
        if (node.isPresentTimes()) {
            node.getTimes().accept(getRealThis());
            out.print(" times ");
        }
        out.print("every ");
        node.getPeriod().accept(getRealThis());
    }

    @Override
    public void handle(final ASTCronExpr node) {
        out.print("cron ");
        out.print("\"");
        out.print(node.getCron().asString());
        out.print("\"");
    }

    @Override
    public void handle(ASTTime node) {
        node.getHours().accept(getRealThis());
        out.print(":");
        node.getMinutes().accept(getRealThis());
        if (node.isPresentSeconds()) {
            out.print(":");
            node.getSeconds().accept(getRealThis());
        }
    }

    @Override
    public void handle(ASTDate node) {
        node.getYear().accept(getRealThis());
        out.print("-");
        node.getMonth().accept(getRealThis());
        out.print("-");
        node.getDay().accept(getRealThis());
    }

    @Override
    public void handle(ASTPeriod node) {
        out.print(node.getPeriod().toString());
    }

}
