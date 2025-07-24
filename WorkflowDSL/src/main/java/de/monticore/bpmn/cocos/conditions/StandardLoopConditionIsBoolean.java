/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTWFStandardLoop;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFStandardLoopCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class StandardLoopConditionIsBoolean implements WorkflowASTWFStandardLoopCoCo {
  
  @Override
  public void check(final ASTWFStandardLoop standardLoop) {
    SymTypeExpression x = WorkflowTypeCheck3.typeOf(standardLoop.getLoopCondition());
    if (!SymTypeRelations.isBoolean(x)) {
      Log.error(Messages.get("0xWFM9002"));
    }
  }
  
}
