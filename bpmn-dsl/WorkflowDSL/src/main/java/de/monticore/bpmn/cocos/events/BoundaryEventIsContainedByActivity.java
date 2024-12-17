package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.se_rwth.commons.logging.Log;

public class BoundaryEventIsContainedByActivity implements WorkflowASTProcessCoCo {

  @Override
  public void check(final ASTProcess process) {
    WorkflowCollectors.toEventsLocal(process).stream()
        .filter(event -> event instanceof ASTEvent)
        .filter(event -> event.getSymbol().isBoundary())
        .forEach(
            event ->
                Log.error(
                    Messages.get("0xWFM1006", event.getName()),
                    event.get_SourcePositionStart(),
                    event.get_SourcePositionEnd()));
  }
}
