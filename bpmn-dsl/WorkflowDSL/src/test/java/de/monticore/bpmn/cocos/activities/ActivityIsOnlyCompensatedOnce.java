 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.workflow._ast.ASTWFActivity;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFActivityCoCo;
import de.monticore.bpmn.workflow._symboltable.WFActivitySymbol;

import java.util.List;
import java.util.Optional;

 // TODO add compensation activity to compensated activity
public class ActivityIsOnlyCompensatedOnce implements WorkflowASTWFActivityCoCo {

  @Override
  public void check(final ASTWFActivity activity) {

  }


}
