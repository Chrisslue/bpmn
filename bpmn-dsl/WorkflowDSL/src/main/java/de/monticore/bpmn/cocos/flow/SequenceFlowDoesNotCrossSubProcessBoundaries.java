package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.se_rwth.commons.logging.Log;

public class SequenceFlowDoesNotCrossSubProcessBoundaries implements WorkflowASTProcessCoCo {

    @Override
    public void check(final ASTProcess process) {
        WorkflowCollectors.toSequenceFlow(process).forEach(this::check);
    }

    public void check(final SequenceFlow sequenceFlow) {
        IWorkflowScope sourceScope = sequenceFlow.getSource().getEnclosingScope();
        IWorkflowScope targetScope = sequenceFlow.getTarget().getEnclosingScope();

        if (isBoundaryEvent(sequenceFlow.getSource())) {
            // Boundary event is contained within activity and connects to flow objects in the scope of the activity
            sourceScope = sourceScope.getEnclosingScope();
        }

        if (!targetScope.equals(sourceScope)) {
            Log.error(Messages.get("0xWFM3003", sequenceFlow.getSource().getName(), sequenceFlow.getTarget().getName()));
        }
    }

    private boolean isBoundaryEvent(final ASTFlowNode flowNode) {
        return flowNode instanceof ASTEvent && ((ASTEvent) flowNode).isBoundary();
    }


}
