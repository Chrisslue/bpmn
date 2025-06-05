 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 249 Description: An Intermediate Event that
 * is attached to the boundary of an Activity can only be used to “catch” the Event trigger.
 */
public class BoundaryEventIsNotThrowing implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.getSymbol().isBoundary() && event.isThrow()) {
      Log.error(
          Messages.get("0xWFM2015", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
