package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow._ast.ASTCallActivity;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.io.IOSpecification;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

/**
 * Creates and adds IO-specifications to symbols.
 */
public class CreateIOSpecification extends WorkflowTransformation implements WorkflowVisitor {

    @Override
    protected void transform() {
        getOutputAst().accept(this);
    }

    @Override
    public void visit(final ASTProcess process) {
        process.getProcessSymbol().setIoSpecification(
                IOSpecification.from(process.getIOSpecification(), process.getEnclosingScope()));
    }

    @Override
    public void visit(final ASTCallActivity callActivity) {
        callActivity.getIOSpecificationOpt().ifPresent(ioSpec ->
                callActivity.getCallActivitySymbol().setIoSpecification(
                        IOSpecification.from(ioSpec, callActivity.getEnclosingScope()))
        );
    }

    @Override
    public void visit(final ASTTask task) {
        task.getIOSpecificationOpt().ifPresent(ioSpec ->
                task.getTaskSymbol().setIoSpecification(
                        IOSpecification.from(ioSpec, task.getEnclosingScope()))
        );
    }

}
