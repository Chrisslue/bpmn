package de.monticore.bpmn.cocos.conditions;

import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.bpmn.workflow._ast.ASTConditionExpression;
import de.monticore.bpmn.workflow._cocos.WorkflowASTConditionExpressionCoCo;
import de.monticore.symboltable.MutableScope;

public class ExpressionHasCorrectTypes implements WorkflowASTConditionExpressionCoCo {

    @Override
    public void check(ASTConditionExpression node) {
        ASTExpression exprNode = node.getExpression();

        ExpressionTypeCheckingVisitor typeChecker = new ExpressionTypeCheckingVisitor((MutableScope) node.getSpannedScope());
        typeChecker.check(exprNode);
    }
}
