/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.trafos;

import static com.google.common.base.Preconditions.checkNotNull;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;

/** Encapsulates a transformation step. */
public abstract class WorkflowTransformation {
  
  private ASTWorkflowCompilationUnit inputAst;
  
  private ASTWorkflowCompilationUnit outputAst;
  
  /**
   * Sets the input AST.
   *
   * @param ast the input AST
   */
  public final void input(final ASTWorkflowCompilationUnit ast) {
    checkNotNull(ast);
    
    inputAst = ast;
  }
  
  /**
   * Executes the transformation.
   *
   * @param ast the output AST.
   */
  public final void transform(ASTWorkflowCompilationUnit ast) {
    checkNotNull(ast);
    
    outputAst = ast;
    
    if (inputAst == null) {
      inputAst = ast;
    }
    
    transform();
  }
  
  /**
   * Returns the input AST.
   *
   * @return the input AST
   */
  protected final ASTWorkflowCompilationUnit getAst() { return inputAst; }
  
  /**
   * Returns the output AST.
   *
   * @return the output AST.
   */
  protected final ASTWorkflowCompilationUnit getOutputAst() { return outputAst; }
  
  /** Executes the transformation logic. */
  protected abstract void transform();
  
}
