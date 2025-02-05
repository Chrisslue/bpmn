package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 258 Description: If the Intermediate Event is
 * attached to the boundary of an Activity: The Intermediate Event MUST NOT be a target for a
 * Sequence Flow; it cannot have an incoming Sequence Flows
 */
public class BoundaryEventHasNoIncomingFlow implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.getSymbol().isBoundary() && !event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2005", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
