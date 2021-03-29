package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._cocos.WorkflowASTActivityCoCo;

// TODO add compensation activity to compensated activity
public class ActivityIsOnlyCompensatedOnce implements WorkflowASTActivityCoCo {

    @Override
    public void check(final ASTActivity activity) {

    }

}
