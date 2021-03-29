package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.conditions.TypeInferingVisitor;
import de.monticore.bpmn.workflow._ast.ASTLoopCardinality;
import de.monticore.bpmn.workflow._cocos.WorkflowASTLoopCardinalityCoCo;
import de.monticore.symboltable.MutableScope;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 191
 * Description: A numeric Expression that controls the number of Activity instances that will be created.
 * This Expression MUST evaluate to an integer.
 */
public class LoopCountExpressionReturnsIntegerNumber implements WorkflowASTLoopCardinalityCoCo {

    @Override
    public void check(final ASTLoopCardinality loopCardinality) {
        loopCardinality.getExpressionOpt().ifPresent(loopExpression -> {
            TypeInferingVisitor typeInferrer = new TypeInferingVisitor((MutableScope) loopExpression.getSpannedScope());

            CDTypeSymbolReference type = typeInferrer.getTypeFromExpression(loopExpression);
            if (type == null) {
                Log.warn(Messages.get("0xWFM1009"));
            }

            if (type != null && !type.getName().equals("Integer")) {
                Log.error(Messages.get("0xWFM1010", type),
                        loopExpression.get_SourcePositionStart(), loopExpression.get_SourcePositionEnd());
            }
        });
    }


}
