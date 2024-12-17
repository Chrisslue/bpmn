package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTLane;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.Stack;

/** Adds a reference to the enclosing lane to flow nodes, if any. */
public class AddReferenceToParentLane extends WorkflowTransformation implements WorkflowVisitor2 {

  private final Stack<Stack<ASTLane>> laneStacks = new Stack<>();

  @Override
  protected void transform() {
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(this);
    getAst().accept(traverser);
  }

  @Override
  public void visit(final ASTProcess astProcess) { // root lane stack
    laneStacks.push(new Stack<>());
  }

  @Override
  public void visit(final ASTSubProcess astSubProcess) { // create new local lane stack
    laneStacks.push(new Stack<>());
  }

  @Override
  public void endVisit(final ASTSubProcess astSubProcess) { // exit local lane stack
    laneStacks.pop();
  }

  @Override
  public void visit(final ASTLane astLane) {
    final Stack<ASTLane> currentStack = laneStacks.peek();
    if (!currentStack.empty()) {
      astLane.getSymbol().setParentLane(currentStack.peek().getName());
    }
    currentStack.push(astLane);
  }

  @Override
  public void endVisit(final ASTLane astLane) {
    final Stack<ASTLane> currentStack = laneStacks.peek();
    currentStack.pop();
  }

  @Override
  public void visit(final ASTFlowElement ASTFlowElement ) {
    final Stack<ASTLane> currentStack = laneStacks.peek();
    if (!currentStack.empty()) {
      ASTFlowElement.setLaneRef(currentStack.peek().getName());
    }
  }
}
