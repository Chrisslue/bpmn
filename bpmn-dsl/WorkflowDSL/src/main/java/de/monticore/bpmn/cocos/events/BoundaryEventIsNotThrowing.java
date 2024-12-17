package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: An Intermediate Event that
 * is attached to the boundary of an Activity can only be used to “catch” the Event trigger.
 */
public class BoundaryEventIsNotThrowing implements WorkflowASTEventCoCo {

  @Override
  public void check(final ASTEvent event) {
    if (event.getSymbol().isBoundary() && event.isThrow()) {
      Log.error(
          Messages.get("0xWFM2015", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
