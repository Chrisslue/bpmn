package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTGatewayType;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowConditionCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class FlowConditionIsBoolean implements WorkflowASTFlowConditionCoCo {
    @Override
    public void check(final ASTFlowCondition condition) {
        if(condition.isPresentExpression()) {
            SymTypeExpression x = WorkflowTypeCheck3.typeOf(condition.getExpression());
            if (!"boolean".equals(x.printFullName())) {
                Log.error(Messages.get("0xWFM9008"));
            }
        }
    }
}
