package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.workflow._ast.ASTWFActivity;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFActivityCoCo;

// TODO add compensation activity to compensated activity
public class ActivityIsOnlyCompensatedOnce implements WorkflowASTWFActivityCoCo {

  @Override
  public void check(final ASTWFActivity activity) {}
}
