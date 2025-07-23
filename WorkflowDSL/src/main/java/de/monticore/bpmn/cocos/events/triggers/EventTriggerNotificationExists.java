/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventTriggerNotificationCoCo;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.monticore.bpmn.workflow._symboltable.WFNotificationSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class EventTriggerNotificationExists implements WorkflowASTWFEventTriggerNotificationCoCo {
  
  @Override
  public void check(final ASTWFEventTriggerNotification notification) {
    String notificationName = notification.getName();
    IWorkflowScope currentScope = notification.getEnclosingScope();
    
    while (currentScope != null) {
      Optional<WFNotificationSymbol> notificationSymbol = currentScope.resolveWFNotification(
          notificationName);
      
      if (notificationSymbol.isPresent()) {
        return;
      }
      else {
        currentScope = currentScope.getEnclosingScope();
      }
    }
    Log.warn(Messages.get("0xWFM2025"));
    
  }
  
}
