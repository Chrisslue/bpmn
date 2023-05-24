package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._cocos.WorkflowASTTaskCoCo;
import de.se_rwth.commons.logging.Log;

public class TaskContainsOnlyBoundaryEvents implements WorkflowASTTaskCoCo {

  @Override
  public void check(final ASTTask task) {
    task.getBoundaryEventList().stream()
        .filter(event -> !event.isBoundary())
        .forEach(
            event ->
                Log.error(
                    Messages.get("0xWFM1007", task.getName()),
                    event.get_SourcePositionStart(),
                    event.get_SourcePositionEnd()));
  }
}
