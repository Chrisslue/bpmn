/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.analysis;

import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

public class IsForCompensationVisitor implements WorkflowHandler {

  protected boolean isForCompensation;

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
  public void handle(final ASTActivity activity) {
    isForCompensation = activity.isForCompensation();
  }

  public boolean isForCompensation() {
    return this.isForCompensation;
  }
}
