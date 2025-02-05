package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFCallActivity;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFCallActivityCoCo;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.se_rwth.commons.logging.Log;
import java.util.Optional;

public class CalledElementDoesExist implements WorkflowASTWFCallActivityCoCo {

  @Override
  public void check(final ASTWFCallActivity callActivity) {
    // enclosing scope is always an Workflow scope
    final WorkflowScope enclosingScope = (WorkflowScope) callActivity.getEnclosingScope();
    final Optional<ASTWFProcess> node =
        enclosingScope.resolveCalledElement(callActivity.getCalledElement());
    if (!node.isPresent()) {
      Log.error(
          Messages.get("0xWFM1008", callActivity.getCalledElement()),
          callActivity.get_SourcePositionStart(),
          callActivity.get_SourcePositionEnd());
    }
  }
}
