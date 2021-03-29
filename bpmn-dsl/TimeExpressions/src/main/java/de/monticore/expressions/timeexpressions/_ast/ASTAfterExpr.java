package de.monticore.expressions.timeexpressions._ast;

public class ASTAfterExpr extends ASTAfterExprTOP {

    protected ASTAfterExpr() {
        super();
    }

    protected ASTAfterExpr(final ASTPeriod value) {
        super(value);
    }

    public String printISO8601() {
        return getPeriod().printISO8601();
    }

}
