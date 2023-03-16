/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.ocl.types.check.OCLDeriver;
import de.monticore.ocl.types.check.OCLSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeRelations;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class WorkflowSTCompleter implements WorkflowVisitor2 {
  @Override
  public void visit(ASTDataObject node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  @Override
  public void visit(ASTError node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  @Override
  public void visit(ASTEscalation node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  @Override
  public void visit(ASTMessage node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  @Override
  public void visit(ASTSignal node) {
    SymTypeExpression typeSymbolRef = createTypeSymbolRef(node.getMCType());

    node.getSymbol().setType(typeSymbolRef);
  }

  protected SymTypeExpression createTypeSymbolRef(ASTMCType astType) {
    TypeCalculator calculator = new TypeCalculator(new OCLSynthesizer(), new OCLDeriver(), new TypeRelations());
    return calculator.symTypeFromAST(astType);
  }
}
