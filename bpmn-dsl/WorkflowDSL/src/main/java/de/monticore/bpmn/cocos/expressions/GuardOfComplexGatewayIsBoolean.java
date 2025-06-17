package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTGatewayType;
import de.monticore.bpmn.workflow._ast.ASTWFMIImplicitEventBehavior;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayTypeCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class GuardOfComplexGatewayIsBoolean implements WorkflowASTGatewayTypeCoCo {
    @Override
    public void check(final ASTGatewayType gatewayType) {
        if(gatewayType.isComplex()) {
            SymTypeExpression x = WorkflowTypeCheck3.typeOf(gatewayType.getGuard());
            if (!"boolean".equals(x.printFullName())) {
                Log.error(Messages.get("0xWFM9006"));
            }
        }
    }
}
