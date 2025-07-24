/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventTriggerNotificationCoCo;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.se_rwth.commons.logging.Log;

public class EventTriggerNotificationExists implements WorkflowASTWFEventTriggerNotificationCoCo {
  
  @Override
  public void check(final ASTWFEventTriggerNotification notification) {
    String notificationName = notification.getName();
    IWorkflowScope currentScope = notification.getEnclosingScope();
    
    if (!currentScope.resolveWFNotification(notificationName).isPresent()) {
      Log.error(Messages.get("0xWFM2025"));
    }
  }
  
}
