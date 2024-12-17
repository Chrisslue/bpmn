package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 258 Description: If the Intermediate Event is
 * attached to the boundary of an Activity: The Intermediate Event MUST be a source for a Sequence
 * Flow. Multiple Sequence Flows MAY originate from an Intermediate Event. If the Intermediate Event
 * is used within normal flow: Intermediate Events MUST be a target of a Sequence Flow. An
 * Intermediate Event MAY have multiple incoming Sequence Flows.
 */
public class IntermediateEventHasOneOrMoreIncomingFlows implements WorkflowASTEventCoCo {

  @Override
  public void check(final ASTEvent event) {
    if (event.isIntermediate() && !event.getSymbol().isBoundary() && event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2020", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
