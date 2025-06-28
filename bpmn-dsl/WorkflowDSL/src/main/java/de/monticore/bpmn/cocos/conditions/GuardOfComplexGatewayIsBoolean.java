package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTGatewayType;
import de.monticore.bpmn.workflow._cocos.WorkflowASTGatewayTypeCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class GuardOfComplexGatewayIsBoolean implements WorkflowASTGatewayTypeCoCo {
    @Override
    public void check(final ASTGatewayType gatewayType) {
        if(gatewayType.isComplex()) {
          SymTypeExpression x = WorkflowTypeCheck3.typeOf(gatewayType.getGuard());
          if(!SymTypeRelations.isBoolean(x)){
            Log.error(Messages.get("0xWFM9006"));
          }
        }
    }
}
