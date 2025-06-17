package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTWFLoopCardinality;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFLoopCardinalityCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class LoopCardinalityExpressionIsBoolean implements WorkflowASTWFLoopCardinalityCoCo {
    @Override
    public void check(final ASTWFLoopCardinality loopCardinality) {
        if(loopCardinality.isPresentExpression()){
            SymTypeExpression x = WorkflowTypeCheck3.typeOf(loopCardinality.getExpression());
            if(!"boolean".equals(x.printFullName())){
                Log.error(Messages.get("0xWFM9005"));
            }
        }

    }
}
