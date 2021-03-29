package de.monticore.bpmn.cocos.conditions;

import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.MutableScope;
import ocl.monticoreocl.ocl._ast.ASTOCLInvariant;

public class ExpressionTypeCheckingVisitor extends ocl.monticoreocl.ocl._types.OCLTypeCheckingVisitor {

    ExpressionTypeCheckingVisitor(MutableScope scope) {
        super(scope);
    }

    /**
     * Checks if the expression is correctly typed.
     *
     * @param exprNode the expression node
     * @see ocl.monticoreocl.ocl._types.OCLTypeCheckingVisitor#checkInvariants(ASTOCLInvariant node, MutableScope scope)
     */
    public void check(ASTExpression exprNode) {
        //checkPrefixExpr(exprNode);
        exprNode.accept(this);
    }

}