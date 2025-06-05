 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 245 Description: The End Event ends the flow
 * of the Process, and thus, will not have any outgoing Sequence Flows—no Sequence Flow can connect
 * from an End Event.
 */
public class EndEventHasNoOutgoingFlow implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isEnd() && !event.isEmptyOutgoings()) {
      Log.error(
          Messages.get("0xWFM2004", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
