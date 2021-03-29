package de.monticore.bpmn.visitors;

import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

/**
 * A visitor that does not traverse contained sub-processes.
 */
public abstract class WorkflowLocalVisitor implements WorkflowVisitor {

    /**
     * The root of the process level to traverse.
     */
    protected final ASTFlowElementContainer localRoot;

    public WorkflowLocalVisitor(final ASTFlowElementContainer localRoot) {
        this.localRoot = localRoot;
    }

    @Override
    public void traverse(final ASTSubProcess subProcess) {
        if (subProcess == localRoot) {
            WorkflowVisitor.super.traverse(subProcess);
        } else { // traverse only attributes of the sub-process, but no elements contained within the the sub-process
            if (subProcess.getAdHocCharacteristicsOpt().isPresent()) {
                subProcess.getAdHocCharacteristicsOpt().get().accept(getRealThis());
            }
            if (null != subProcess.getIOSpecification()) {
                subProcess.getIOSpecification().accept(getRealThis());
            }
            if (subProcess.getCompensationHandlerOpt().isPresent()) {
                subProcess.getCompensationHandlerOpt().get().accept(getRealThis());
            }
            if (subProcess.getLoopCharacteristicsOpt().isPresent()) {
                subProcess.getLoopCharacteristicsOpt().get().accept(getRealThis());
            }
        }
    }

}
