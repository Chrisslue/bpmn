 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 258 Description: If the Intermediate Event is
 * attached to the boundary of an Activity: The Intermediate Event MUST be a source for a Sequence
 * Flow. Multiple Sequence Flows MAY originate from an Intermediate Event. If the Intermediate Event
 * is used within normal flow: Intermediate Events MUST be a target of a Sequence Flow. An
 * Intermediate Event MAY have multiple incoming Sequence Flows.
 */
public class IntermediateEventHasOneOrMoreIncomingFlows implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isIntermediate() && !event.getSymbol().isBoundary() && event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2020", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
