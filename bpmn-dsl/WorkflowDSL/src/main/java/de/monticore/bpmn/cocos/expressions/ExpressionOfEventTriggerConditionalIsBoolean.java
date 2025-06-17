package de.monticore.bpmn.cocos.expressions;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._ast.ASTAdHocCharacteristics;
import de.monticore.bpmn.workflow._ast.ASTWFEventTriggerConditional;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventTriggerConditionalCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class ExpressionOfEventTriggerConditionalIsBoolean implements WorkflowASTWFEventTriggerConditionalCoCo {

    @Override
    public void check(final ASTWFEventTriggerConditional eventTriggerConditional) {
        SymTypeExpression x = WorkflowTypeCheck3.typeOf(eventTriggerConditional.getExpression());
        if(!"boolean".equals(x.printFullName())){
            Log.error(Messages.get("0xWFM9007"));
        }
    }
}
