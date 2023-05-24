package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 29 Description: End events can only create
 * ("throw") a trigger
 */
public class EndEventIsNotCatching implements WorkflowASTEventCoCo {

  @Override
  public void check(final ASTEvent event) {
    if (event.isEnd() && event.isCatch()) {
      Log.error(
          Messages.get("0xWFM2002", event.getName()),
          event.get_SourcePositionStart(),
          event.get_SourcePositionEnd());
    }
  }
}
