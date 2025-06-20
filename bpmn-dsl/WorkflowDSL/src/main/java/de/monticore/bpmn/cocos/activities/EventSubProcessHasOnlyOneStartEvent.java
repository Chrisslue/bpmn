/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 174 Description: An Event Sub-Process MUST
 * have one and only one Start Event
 */
public class EventSubProcessHasOnlyOneStartEvent implements WorkflowASTWFSubProcessCoCo {
  
  @Override
  public void check(final ASTWFSubProcess subProcess) {
    /*
    if (subProcess.getSymbol().isTriggeredByEvent()) {
      Collection<ASTWFEvent> startEvents = WorkflowCollectors.toStartEventsLocalSubProcess(subProcess);
      if (startEvents.size() > 1) {
        startEvents.forEach(
            event ->
                Log.error(
                    Messages.get("0xWFM4001", subProcess.getName()),
                    event.get_SourcePositionStart(),
                    event.get_SourcePositionEnd()));
      }
    }
    */
  }
  
}
