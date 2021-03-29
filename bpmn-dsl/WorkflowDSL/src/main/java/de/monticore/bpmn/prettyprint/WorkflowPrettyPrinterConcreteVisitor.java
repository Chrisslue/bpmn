package de.monticore.bpmn.prettyprint;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.prettyprint.IndentPrinter;

/**
 * Pretty printer for BPMN.
 */
// TODO provide complete pretty printing
public class WorkflowPrettyPrinterConcreteVisitor implements WorkflowVisitor {

    protected WorkflowVisitor realThis;

    protected IndentPrinter out;

    public WorkflowPrettyPrinterConcreteVisitor(IndentPrinter o) {
        realThis = this;
        out = o;
    }

    @Override
    public WorkflowVisitor getRealThis() {
        return realThis;
    }

    @Override
    public void setRealThis(WorkflowVisitor realThis) {
        this.realThis = realThis;
    }

    @Override
    public void handle(ASTProcess node) {
        out.println("process " + node.getName() + " {");
        out.indent();
        traverse(node);
        out.unindent();
        out.println("}");
    }

    @Override
    public void handle(ASTSubProcess node) {
        out.println("subprocess " + node.getName() + " {");
        out.indent();
        traverse(node);
        out.unindent();
        out.println("}");
    }

    @Override
    public void handle(ASTTask node) {
        out.print("task " + node.getName() + " ");
        traverse(node);
        out.println(";");
    }

    @Override
    public void handle(ASTNamedGateway node) {
        out.print("gateway " + node.getName() + " ");
        traverse(node);
        out.println(";");
    }

    @Override
    public void handle(ASTInlineGateway node) {
        out.print("gateway " + node.getName() + " ");
        traverse(node);
        out.println(";");
    }

    @Override
    public void handle(ASTNamedEvent node) {
        out.print("event " + node.getName() + " ");
        traverse(node);
        out.println(";");
    }

    @Override
    public void handle(ASTInlineEvent node) {
        out.print("event " + node.getName() + " ");
        traverse(node);
        out.println(";");
    }

    @Override
    public void endVisit(ASTSequenceFlow node) {
        out.println(";");
    }

    @Override
    public void handle(ASTConditionExpression node) {
        out.print("[");
        traverse(node);
        out.print("]");
    }

    @Override
    public void handle(ASTTimerExpression node) {
        out.print("[");
        traverse(node);
        out.print("]");
    }

}
