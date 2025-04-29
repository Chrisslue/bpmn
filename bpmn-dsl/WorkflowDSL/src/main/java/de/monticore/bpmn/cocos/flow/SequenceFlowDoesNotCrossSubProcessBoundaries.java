 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.se_rwth.commons.logging.Log;

public class SequenceFlowDoesNotCrossSubProcessBoundaries implements WorkflowASTWFProcessCoCo {

  /* method toSequenceFlow does not work as we do not have FlowElementContainers anymore */
  @Override
  public void check(final ASTWFProcess process) {
    //WorkflowCollectors.toSequenceFlow(process).forEach(this::check);
  }

  public void check(final SequenceFlow sequenceFlow) {
    IWorkflowScope sourceScope = sequenceFlow.getSource().getEnclosingScope();
    IWorkflowScope targetScope = sequenceFlow.getTarget().getEnclosingScope();

    if (isBoundaryEvent(sequenceFlow.getSource())) {
      // Boundary event is contained within activity and connects to flow objects in the scope of
      // the activity
      sourceScope = sourceScope.getEnclosingScope();
    }

    if (!targetScope.equals(sourceScope)) {
      Log.error(
          Messages.get(
              "0xWFM3003", sequenceFlow.getSource().getName(), sequenceFlow.getTarget().getName()));
    }
  }

  private boolean isBoundaryEvent(final ASTFlowElement flowNode) {
    return flowNode instanceof ASTWFEvent && ((ASTWFEvent) flowNode).getSymbol().isBoundary();
  }
}
