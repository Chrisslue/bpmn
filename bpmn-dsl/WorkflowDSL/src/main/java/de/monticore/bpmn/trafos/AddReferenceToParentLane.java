 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFLane;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.Stack;

/** Adds a reference to the enclosing lane to flow nodes, if any. */
public class AddReferenceToParentLane extends WorkflowTransformation implements WorkflowVisitor2 {

  private final Stack<Stack<ASTWFLane>> laneStacks = new Stack<>();

  @Override
  protected void transform() {
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(this);
    getAst().accept(traverser);
  }

  @Override
  public void visit(final ASTWFProcess astProcess) { // root lane stack
    laneStacks.push(new Stack<>());
  }

  @Override
  public void visit(final ASTWFSubProcess astSubProcess) { // create new local lane stack
    laneStacks.push(new Stack<>());
  }

  @Override
  public void endVisit(final ASTWFSubProcess astSubProcess) { // exit local lane stack
    laneStacks.pop();
  }

  @Override
  public void visit(final ASTWFLane astLane) {
    final Stack<ASTWFLane> currentStack = laneStacks.peek();
    currentStack.push(astLane);
  }

  @Override
  public void endVisit(final ASTWFLane astLane) {
    final Stack<ASTWFLane> currentStack = laneStacks.peek();
    currentStack.pop();
  }

  @Override
  public void visit(final ASTFlowElement ASTFlowElement ) {
    final Stack<ASTWFLane> currentStack = laneStacks.peek();
    if (!currentStack.empty()) {
      ASTFlowElement.setLaneRef(currentStack.peek().getName());
    }
  }
}
