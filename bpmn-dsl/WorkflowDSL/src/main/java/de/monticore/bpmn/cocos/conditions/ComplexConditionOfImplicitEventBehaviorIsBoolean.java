/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTWFMIImplicitEventBehavior;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFMIImplicitEventBehaviorCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class ComplexConditionOfImplicitEventBehaviorIsBoolean implements
    WorkflowASTWFMIImplicitEventBehaviorCoCo {
  
  @Override
  public void check(final ASTWFMIImplicitEventBehavior implicitEventBehavior) {
    if (implicitEventBehavior.isPresentComplexCondition()) {
      SymTypeExpression x = WorkflowTypeCheck3.typeOf(implicitEventBehavior.getComplexCondition());
      if (!SymTypeRelations.isBoolean(x)) {
        Log.error(Messages.get("0xWFM9004"));
      }
    }
    
  }
  
}
