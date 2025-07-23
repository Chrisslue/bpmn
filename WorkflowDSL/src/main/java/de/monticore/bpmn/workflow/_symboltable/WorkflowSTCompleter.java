/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;

import java.util.Optional;

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
  
  @Override
  public void visit(ASTWFTask node) {
    for (ASTWFEvent event : node.getBoundaryEventList()) {
      // set boundary events
      event.getSymbol().setBoundary(true);
      
      // set compensation and compensates
      if (event.isPresentCompensationHandler()) {
        String activityName = event.getCompensationHandler().getActivity();
        Optional<WFActivitySymbol> activitySymbol = event.getCompensationHandler()
            .getEnclosingScope().resolveWFActivity(activityName);
        if (activitySymbol.isPresent()) {
          ASTWFActivity activity = activitySymbol.get().getAstNode();
          activity.getSymbol().setCompensating(true);
          activity.getSymbol().setCompensates(node.getSymbol());
        }
      }
    }
  }
  
  @Override
  public void visit(ASTWFCallActivity node) {
    for (ASTWFEvent event : node.getBoundaryEventList()) {
      // set boundary events
      event.getSymbol().setBoundary(true);
      
      // set compensation and compensates
      if (event.isPresentCompensationHandler()) {
        String activityName = event.getCompensationHandler().getActivity();
        Optional<WFActivitySymbol> activitySymbol = event.getCompensationHandler()
            .getEnclosingScope().resolveWFActivity(activityName);
        if (activitySymbol.isPresent()) {
          ASTWFActivity activity = activitySymbol.get().getAstNode();
          activity.getSymbol().setCompensating(true);
          activity.getSymbol().setCompensates(node.getSymbol());
        }
      }
    }
  }
  
  @Override
  public void visit(ASTWFSubProcess node) {
    for (ASTWFEvent event : node.getBoundaryEventList()) {
      // set boundary events
      event.getSymbol().setBoundary(true);
      
      // set compensation and compensates
      if (event.isPresentCompensationHandler()) {
        String activityName = event.getCompensationHandler().getActivity();
        Optional<WFActivitySymbol> activitySymbol = event.getCompensationHandler()
            .getEnclosingScope().resolveWFActivity(activityName);
        if (activitySymbol.isPresent()) {
          ASTWFActivity activity = activitySymbol.get().getAstNode();
          activity.getSymbol().setCompensating(true);
          activity.getSymbol().setCompensates(node.getSymbol());
        }
      }
    }
  }
  
  protected SymTypeExpression createTypeSymbolRef(ASTMCType astType) {
    WorkflowTypeCheck3.init();
    return WorkflowTypeCheck3.symTypeFromAST(astType);
    
  }
  
}
