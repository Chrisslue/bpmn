 /* (c) https://github.com/MontiCore/monticore */ 
/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.analysis;

import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

public class IsEventSubProcessVisitor implements WorkflowHandler {

  protected boolean isEventSubProcess;

  protected WorkflowTraverser traverser;

  @Override
  public WorkflowTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(WorkflowTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(final ASTWFSubProcess subProcess) {
    //isEventSubProcess = subProcess.getSymbol().isTriggeredByEvent();
    isEventSubProcess = false;
  }

  public boolean isEventSubProcess() {
    return isEventSubProcess;
  }
}
