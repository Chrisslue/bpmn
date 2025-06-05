 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 244 Description: A Start Event MUST be a
 * source for a Sequence Flow. Multiple Sequence Flows MAY originate from a Start Event.
 */
public class StartEventHasOneOrMoreOutgoingFlows implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isStart() && event.isEmptyOutgoings()) {
      Log.error(
          Messages.get("0xWFM2008", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
