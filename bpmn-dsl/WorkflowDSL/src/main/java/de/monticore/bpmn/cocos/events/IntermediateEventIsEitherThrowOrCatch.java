package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: The Event can respond to
 * (“catch”) the Event trigger xor the Event can be used to set off (“throw”) the Event trigger.
 */
public class IntermediateEventIsEitherThrowOrCatch implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isIntermediate() && !event.getSymbol().isBoundary() && !event.isCatch() && !event.isThrow()) {
      Log.error(
          Messages.get("0xWFM2017", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
