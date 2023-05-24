package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 174 Description: An Event Sub-Process MUST
 * have one and only one Start Event
 */
public class EventSubProcessHasOnlyOneStartEvent implements WorkflowASTSubProcessCoCo {

  @Override
  public void check(final ASTSubProcess subProcess) {
    if (subProcess.isTriggeredByEvent()) {
      Collection<ASTEvent> startEvents = WorkflowCollectors.toStartEventsLocal(subProcess);
      if (startEvents.size() > 1) {
        startEvents.forEach(
            event ->
                Log.error(
                    Messages.get("0xWFM4001", subProcess.getName()),
                    event.get_SourcePositionStart(),
                    event.get_SourcePositionEnd()));
      }
    }
  }
}
