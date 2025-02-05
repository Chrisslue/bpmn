/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.TypeCheck3;

public class WorkflowSTCompleter implements WorkflowVisitor2 {
  @Override
  public void visit(ASTWFDataObject node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  @Override
  public void visit(ASTWFNotification node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
    node.getSymbol().setIsError(node.getKind() == ASTConstantsWorkflow.ERROR);
    node.getSymbol().setIsMessage(node.getKind() == ASTConstantsWorkflow.MESSAGE);
    node.getSymbol().setIsSignal(node.getKind() == ASTConstantsWorkflow.SIGNAL);
    node.getSymbol().setIsEscalation(node.getKind() == ASTConstantsWorkflow.ESCALATION);
  }

  protected SymTypeExpression createTypeSymbolRef(ASTMCType astType) {
    return TypeCheck3.symTypeFromAST(astType);
  }
}
