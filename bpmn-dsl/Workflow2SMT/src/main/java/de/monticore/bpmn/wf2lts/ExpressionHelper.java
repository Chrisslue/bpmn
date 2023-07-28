package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import java.util.List;

public class ExpressionHelper {

  private ExpressionHelper() {
  }

  public static ASTExpression toExpression(ASTFlowCondition condition) {
    return WorkflowMill
        .typeDispatcher()
        .asASTConditionExpression(condition.getCondition()).getExpression();
  }

  public static ASTExpression mergeConditions(List<ASTFlowCondition> conditions) {
    return conditions.stream()
        .map(ExpressionHelper::toExpression)
        .reduce((astCondition, astCondition2) ->
            WorkflowMill.binaryAndExpressionBuilder().setLeft(astCondition).setRight(astCondition2).setOperator("&&")
                .build()).orElseGet(() -> {
              Log.error("Could not combine List<ASTExpression>");
              return null;
            }
        );
  }

}
