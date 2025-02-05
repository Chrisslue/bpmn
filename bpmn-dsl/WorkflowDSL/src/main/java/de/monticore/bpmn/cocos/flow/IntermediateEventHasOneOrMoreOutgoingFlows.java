package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 258 Description: An Intermediate Event MUST
 * be a source for a Sequence Flow. Multiple Sequence Flows MAY originate from an Intermediate
 * Event.
 */
public class IntermediateEventHasOneOrMoreOutgoingFlows implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isIntermediate() && event.isEmptyOutgoings()) {
      Log.error(
          Messages.get("0xWFM2021", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
