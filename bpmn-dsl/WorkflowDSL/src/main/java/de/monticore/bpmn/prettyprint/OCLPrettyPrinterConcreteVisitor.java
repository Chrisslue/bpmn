package de.monticore.bpmn.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import ocl.monticoreocl.ocl._ast.ASTOCLMethodDeclaration;
import ocl.monticoreocl.ocl._ast.ASTOCLVariableDeclaration;
import ocl.monticoreocl.ocl._visitor.OCLVisitor;

/**
 * Pretty printer for OCL.
 */
public class OCLPrettyPrinterConcreteVisitor implements OCLVisitor {

    protected OCLVisitor realThis;

    protected IndentPrinter out;

    public OCLPrettyPrinterConcreteVisitor(IndentPrinter o) {
        realThis = this;
        out = o;
    }

    @Override
    public OCLVisitor getRealThis() {
        return realThis;
    }

    @Override
    public void setRealThis(OCLVisitor realThis) {
        this.realThis = realThis;
    }

    @Override
    public void handle(ASTOCLVariableDeclaration node) {
        if (node.isPresentType()) {
            node.getType().accept(getRealThis());
        }
        node.getName2().accept(getRealThis());
        out.print(" = ");
        node.getExpression().accept(getRealThis());
    }

    @Override
    public void visit(ASTOCLMethodDeclaration node) {
        out.print(node.getName());
        out.print(" = ");
    }

}
