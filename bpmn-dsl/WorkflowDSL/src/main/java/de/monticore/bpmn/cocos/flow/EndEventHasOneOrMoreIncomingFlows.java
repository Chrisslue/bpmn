 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 248 Description: An End Event MUST be a
 * target for a Sequence Flow. An End Event MAY have multiple incoming Sequence Flows
 */
public class EndEventHasOneOrMoreIncomingFlows implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isEnd() && event.isEmptyIncomings()) {
      Log.error(
          Messages.get("0xWFM2009", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
