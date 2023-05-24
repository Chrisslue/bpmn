package de.monticore.expressions.timeexpressions._ast;

public class ASTAfterExpr extends ASTAfterExprTOP {

  public String printISO8601() {
    return getPeriod().printISO8601();
  }
}
