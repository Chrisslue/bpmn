/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTWFMILoop;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFMILoopCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class MILoopCompletionConditionIsBoolean implements WorkflowASTWFMILoopCoCo {

    @Override
    public void check(final ASTWFMILoop multiInstanceLoop) {
        if(multiInstanceLoop.isPresentCompletionCondition()) {
            SymTypeExpression x = WorkflowTypeCheck3.typeOf(multiInstanceLoop.getCompletionCondition());
            if (!"boolean".equals(x.printFullName())) {
                Log.error(Messages.get("0xWFM9003"));
            }
        }
    }
}
