package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 248 Description: An End Event MUST be a
 * target for a Sequence Flow. An End Event MAY have multiple incoming Sequence Flows
 */
public class EndEventHasOneOrMoreIncomingFlows implements WorkflowASTEventCoCo {

  @Override
  public void check(final ASTEvent event) {
    if (event.isEnd() && event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2009", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
