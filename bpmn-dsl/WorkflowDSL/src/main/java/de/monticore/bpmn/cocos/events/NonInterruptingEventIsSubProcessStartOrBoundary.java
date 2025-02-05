package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

public class NonInterruptingEventIsSubProcessStartOrBoundary
    implements WorkflowASTWFProcessCoCo, WorkflowASTWFSubProcessCoCo {

  @Override
  public void check(final ASTWFProcess process) {
    WorkflowCollectors.toEventsLocal(process).stream()
        .filter(ASTWFEvent::isNoninterrupt)
        .forEach(this::logError);
  }

  @Override
  public void check(final ASTWFSubProcess subProcess) {
    WorkflowCollectors.toEventsLocalSubProcess(subProcess).stream()
        .filter(ASTWFEvent::isNoninterrupt)
        .filter(event -> (event.isIntermediate() && !event.getSymbol().isBoundary()) || event.isEnd())
        .forEach(this::logError);
  }

  private void logError(final ASTWFEvent event) {
    Log.error(
        Messages.get("0xWFM2018", event.getName()),
        event.get_SourcePositionStart(),
        event.get_SourcePositionEnd());
  }
}
