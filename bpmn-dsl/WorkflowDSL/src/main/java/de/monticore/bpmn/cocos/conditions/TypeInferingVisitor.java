package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.workflow._ast.ASTCondition;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.symboltable.MutableScope;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import ocl.monticoreocl.ocl._types.OCLExpressionTypeInferingVisitor;

public class TypeInferingVisitor implements WorkflowInheritanceVisitor {

    protected final MutableScope scope;

    private CDTypeSymbolReference returnTypeRef;

    public TypeInferingVisitor(MutableScope scope) {
        this.scope = scope;
    }

    public CDTypeSymbolReference getTypeFromExpression(ASTCondition node) {
        node.accept(this);

        return returnTypeRef;
    }

    @Override
    public void visit(final ASTExpression expression) {
        returnTypeRef = new OCLExpressionTypeInferingVisitor(scope).getTypeFromExpression(expression);
    }

}