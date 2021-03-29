package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTLane;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;

import java.util.Stack;

/**
 * Adds a reference to the enclosing lane to flow nodes, if any.
 */
public class AddReferenceToParentLane extends WorkflowTransformation implements WorkflowInheritanceVisitor {

    private final Stack<Stack<ASTLane>> laneStacks = new Stack<>();

    @Override
    protected void transform() {
        getAst().accept(this);
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
            astLane.setParentLane(currentStack.peek().getName());
        }
        currentStack.push(astLane);
    }

    @Override
    public void endVisit(final ASTLane astLane) {
        final Stack<ASTLane> currentStack = laneStacks.peek();
        currentStack.pop();
    }

    @Override
    public void visit(final ASTFlowNode astFlowNode) {
        final Stack<ASTLane> currentStack = laneStacks.peek();
        if (!currentStack.empty()) {
            astFlowNode.setLaneRef(currentStack.peek().getName());
        }
    }

}
