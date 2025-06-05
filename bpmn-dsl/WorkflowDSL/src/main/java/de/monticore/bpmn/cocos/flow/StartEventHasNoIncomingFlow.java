 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 237 Description: The Start Event starts the
 * flow of the Process, and thus, will not have any incoming Sequence Flows — no Sequence Flow can
 * connect to a Start Event.
 */
public class StartEventHasNoIncomingFlow implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isStart() && !event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2003", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
