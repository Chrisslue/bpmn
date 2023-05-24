package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTConditionExpression;
import de.monticore.bpmn.workflow._ast.ASTLoopCardinality;
import de.monticore.bpmn.workflow._cocos.WorkflowASTLoopCardinalityCoCo;
import de.monticore.ocl.types.check.OCLDeriver;
import de.monticore.ocl.types.check.OCLSynthesizer;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types.check.TypeRelations;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 191 Description: A numeric Expression that
 * controls the number of Activity instances that will be created. This Expression MUST evaluate to
 * an integer.
 */
public class LoopCountExpressionReturnsIntegerNumber implements WorkflowASTLoopCardinalityCoCo {

  @Override
  public void check(final ASTLoopCardinality loopCardinality) {
    if (loopCardinality.isPresentExpression()) {
      ASTConditionExpression loopExpression =
          (ASTConditionExpression) loopCardinality.getExpression();
      TypeCalculator calculator =
          new TypeCalculator(new OCLSynthesizer(), new OCLDeriver(), new TypeRelations());
      SymTypeExpression type = calculator.typeOf(loopExpression.getExpression());

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
