/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFInlineGateway;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/** Adds anonymous names to in-lined gateways and events. */
public class AddNameToInlineFlowNodes extends WorkflowTransformation implements WorkflowVisitor2 {
  
  private int nextAnonymousId = 1;
  
  private Integer getNextId() { return nextAnonymousId++; }
  
  @Override
  protected void transform() {
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(this);
    getAst().accept(traverser);
  }
  
  @Override
  public void visit(final ASTWFInlineGateway gateway) {
    gateway.setName("_Gateway_" + getNextId());
  }
  
}
