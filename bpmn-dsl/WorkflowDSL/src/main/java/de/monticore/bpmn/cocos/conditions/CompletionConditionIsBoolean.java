/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTAdHocCharacteristics;
import de.monticore.bpmn.workflow._cocos.WorkflowASTAdHocCharacteristicsCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class CompletionConditionIsBoolean implements WorkflowASTAdHocCharacteristicsCoCo {


    @Override
    public void check(final ASTAdHocCharacteristics characteristics) {
        SymTypeExpression x = WorkflowTypeCheck3.typeOf(characteristics.getCompletionCondition());
        if(!SymTypeRelations.isBoolean(x)){
          Log.error(Messages.get("0xWFM9001"));
        }
        
    }
}
