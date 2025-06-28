/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTConstantsWorkflow;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFTaskCoCo;
import de.se_rwth.commons.logging.Log;

public class TaskTypeAttributesAreSet implements WorkflowASTWFTaskCoCo {
  
  @Override
  public void check(final ASTWFTask task) {
    if (task.getType() == ASTConstantsWorkflow.SERVICE) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.hasSpecifiedAttributeWebservice() || task.getTaskTypeAttributes()
            .isPresentMessage()) {
          Log.error(Messages.get("0xWFM6002", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6002", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.SEND) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.getTaskTypeAttributes().isPresentMessage() || !task.getTaskTypeAttributes()
            .isPresentOperation()) {
          Log.error(Messages.get("0xWFM6003", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6003", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.RECEIVE) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.getTaskTypeAttributes().isPresentMessage() || !task.getTaskTypeAttributes()
            .isPresentOperation()) {
          Log.error(Messages.get("0xWFM6004", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6004", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.USER) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.hasSpecifiedAttributeWebservice() || task.getTaskTypeAttributes()
            .isPresentOperation() || task.getTaskTypeAttributes().isPresentMessage()) {
          Log.error(Messages.get("0xWFM6005", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6005", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.MANUAL) {
      if (task.isPresentTaskTypeAttributes()) {
        if (task.getTaskTypeAttributes().getResourcesList().isEmpty()) {
          Log.error(Messages.get("0xWFM6006", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6006", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.RULE) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.hasSpecifiedAttributeWebservice() || task.getTaskTypeAttributes()
            .isPresentOperation() || task.getTaskTypeAttributes().isPresentMessage()) {
          Log.error(Messages.get("0xWFM6007", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6007", task.getName()));
      }
    }
    else if (task.getType() == ASTConstantsWorkflow.SCRIPT) {
      if (task.isPresentTaskTypeAttributes()) {
        if (!task.getTaskTypeAttributes().isPresentScript()) {
          Log.error(Messages.get("0xWFM6008", task.getName()));
        }
      }
      else {
        Log.error(Messages.get("0xWFM6008", task.getName()));
      }
    }
    else {
      if (task.getType() != 0) {
        Log.error("Something went wrong!");
      }
    }
  }
  
}
