package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

public class NonInterruptingEventIsSubProcessStartOrBoundary
    implements WorkflowASTProcessCoCo, WorkflowASTSubProcessCoCo {

  @Override
  public void check(final ASTProcess process) {
    WorkflowCollectors.toEventsLocal(process).stream()
        .filter(ASTEvent::isNonInterrupt)
        .forEach(this::logError);
  }

  @Override
  public void check(final ASTSubProcess subProcess) {
    WorkflowCollectors.toEventsLocal(subProcess).stream()
        .filter(ASTEvent::isNonInterrupt)
        .filter(event -> (event.isIntermediate() && !event.isBoundary()) || event.isEnd())
        .forEach(this::logError);
  }

  private void logError(final ASTEvent event) {
    Log.error(
        Messages.get("0xWFM2018", event.getName()),
        event.get_SourcePositionStart(),
        event.get_SourcePositionEnd());
  }
}
