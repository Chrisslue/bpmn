/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTAdHocCharacteristics;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTWFEventTriggerConditional;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTAdHocCharacteristicsCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowConditionCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class CompletionConditionIsBoolean implements WorkflowASTAdHocCharacteristicsCoCo {


    @Override
    public void check(final ASTAdHocCharacteristics characteristics) {
        SymTypeExpression x = WorkflowTypeCheck3.typeOf(characteristics.getCompletionCondition());
        if(!"boolean".equals(x.printFullName())){
            Log.error(Messages.get("0xWFM9001"));
        }
    }
}
