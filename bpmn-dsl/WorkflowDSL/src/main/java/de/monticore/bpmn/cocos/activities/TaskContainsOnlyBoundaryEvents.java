 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFTaskCoCo;
import de.se_rwth.commons.logging.Log;

public class TaskContainsOnlyBoundaryEvents implements WorkflowASTWFTaskCoCo {

  @Override
  public void check(final ASTWFTask task) {
    task.getBoundaryEventList().stream()
        .filter(event -> !event.getSymbol().isBoundary())
        .forEach(
            event ->
                Log.error(
                    Messages.get("0xWFM1007", task.getName()),
                    event.get_SourcePositionStart(),
                    event.get_SourcePositionEnd()));
  }
}
