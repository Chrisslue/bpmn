package de.monticore.bpmn.visitors;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

import java.util.Iterator;

/**
 * A visitor that does not traverse contained sub-processes.
 */
public abstract class WorkflowLocalVisitor implements WorkflowVisitor2, WorkflowHandler {
    
    protected WorkflowTraverser traverser;

    @Override
    public WorkflowTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(WorkflowTraverser traverser) {
        this.traverser = traverser;
    }

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
            if (subProcess.isPresentAdHocCharacteristics()) {
                subProcess.getAdHocCharacteristics().accept(getTraverser());
            }
            if (null != subProcess.getIOSpecification()) {
                subProcess.getIOSpecification().accept(getTraverser());
            }
            for (ASTLane astLane : subProcess.getLaneList()) {
                astLane.accept(getTraverser());
            }
            for (ASTFlowElement astFlowElement : subProcess.getFlowElementList()) {
                astFlowElement.accept(getTraverser());
            }
            if (subProcess.isPresentCompensationHandler()) {
                subProcess.getCompensationHandler().accept(getTraverser());
            }
            if (subProcess.isPresentLoopCharacteristics()) {
                subProcess.getLoopCharacteristics().accept(getTraverser());
            }
            for (SequenceFlow sequenceFlow : subProcess.getIncomingsList()) {
                sequenceFlow.accept(getTraverser());
            }
            for (SequenceFlow sequenceFlow : subProcess.getOutgoingsList()) {
                sequenceFlow.accept(getTraverser());
            }

        } else { // traverse only attributes of the sub-process, but no elements contained within the the sub-process
            if (subProcess.isPresentAdHocCharacteristics()) {
                subProcess.getAdHocCharacteristics().accept(getTraverser());
            }
            if (null != subProcess.getIOSpecification()) {
                subProcess.getIOSpecification().accept(getTraverser());
            }
            if (subProcess.isPresentCompensationHandler()) {
                subProcess.getCompensationHandler().accept(getTraverser());
            }
            if (subProcess.isPresentLoopCharacteristics()) {
                subProcess.getLoopCharacteristics().accept(getTraverser());
            }
        }
    }

}
