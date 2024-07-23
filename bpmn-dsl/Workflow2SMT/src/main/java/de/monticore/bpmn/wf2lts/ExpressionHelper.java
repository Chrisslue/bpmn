package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import java.util.List;

public class ExpressionHelper {

  private ExpressionHelper() {}

  public static ASTExpression mergeConditions(List<ASTFlowCondition> conditions) {
    return conditions.stream()
        .map(ASTFlowCondition::getExpression)
        .reduce(
            (astExpression, nextExpression) ->
                WorkflowMill.binaryAndExpressionBuilder()
                    .setLeft(astExpression)
                    .setRight(nextExpression)
                    .setOperator("&&")
                    .build())
        .orElseGet(
            () -> {
              Log.error("Could not combine List<ASTExpression>");
              return null;
            });
  }
}
