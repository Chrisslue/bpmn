 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFLoopCardinality;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFLoopCardinalityCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 191 Description: A numeric Expression that
 * controls the number of Activity instances that will be created. This Expression MUST evaluate to
 * an integer.
 */
public class LoopCountExpressionReturnsIntegerNumber implements WorkflowASTWFLoopCardinalityCoCo {

  @Override
  public void check(final ASTWFLoopCardinality loopCardinality) {
    if (loopCardinality.isPresentExpression()) {
      ASTExpression loopExpression = loopCardinality.getExpression();
      SymTypeExpression type = TypeCheck3.typeOf(loopExpression);

      if (type == null) {
        Log.warn(Messages.get("0xWFM1009"));
      }

      if (type != null && !new TypeRelations().isInt(type)) {
        Log.error(
            Messages.get("0xWFM1010", type.print()),
            loopExpression.get_SourcePositionStart(),
            loopExpression.get_SourcePositionEnd());
      }
    }
  }
}
