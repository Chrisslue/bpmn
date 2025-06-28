package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTWFLoopCardinality;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFLoopCardinalityCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

public class LoopCardinalityExpressionIsBoolean implements WorkflowASTWFLoopCardinalityCoCo {
    @Override
    public void check(final ASTWFLoopCardinality loopCardinality) {
        
        if(loopCardinality.isPresentExpression()){
          SymTypeExpression x = WorkflowTypeCheck3.typeOf(loopCardinality.getExpression());
          if(!SymTypeRelations.isInt(x)){
            Log.error(Messages.get("0xWFM9005"));
          }
        }
        
        

    }
}
