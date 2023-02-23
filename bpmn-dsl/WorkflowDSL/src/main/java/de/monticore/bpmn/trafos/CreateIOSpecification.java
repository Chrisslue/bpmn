package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTCallActivity;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.io.IOSpecification;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Creates and adds IO-specifications to symbols.
 */
public class CreateIOSpecification extends WorkflowTransformation implements WorkflowVisitor2 {

    @Override
    protected void transform() {
        WorkflowTraverser traverser = WorkflowMill.traverser();
        traverser.add4Workflow(this);
        getOutputAst().accept(traverser);
    }

    @Override
    public void visit(final ASTProcess process) {
        process.getSymbol().setIoSpecification(
                IOSpecification.from(process.getIOSpecification(), process.getEnclosingScope()));
    }

    @Override
    public void visit(final ASTCallActivity callActivity) {
        if(callActivity.isPresentIOSpecification()){
            callActivity.getSymbol().setIoSpecification(IOSpecification.from(callActivity.getIOSpecification(),
              callActivity.getEnclosingScope()));
        }
    }

    @Override
    public void visit(final ASTTask task) {
        if(task.isPresentIOSpecification()){
            task.getSymbol().setIoSpecification(
              IOSpecification.from(task.getIOSpecification(), task.getEnclosingScope())
            );
        }
    }

}
