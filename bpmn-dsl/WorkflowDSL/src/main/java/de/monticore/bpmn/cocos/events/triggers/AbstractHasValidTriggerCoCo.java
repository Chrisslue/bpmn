package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.se_rwth.commons.logging.Log;

class AbstractHasValidTriggerCoCo {

  private final String errorCode;

  AbstractHasValidTriggerCoCo(final String errorCode) {
    this.errorCode = errorCode;
  }

  protected void logError(final ASTWFEvent event) {
    Log.error(
        Messages.get(errorCode, event.getName()),
        event.get_SourcePositionStart(),
        event.get_SourcePositionEnd());
  }
}
