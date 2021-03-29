package de.monticore.bpmn.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import ocl.monticoreocl.oclexpressions._ast.ASTOCLQualifiedPrimary;
import ocl.monticoreocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;

/**
 * Pretty printer for OCL expressions.
 */
public class OCLExpressionsPrettyPrinterConcreteVisitor extends OCLExpressionsPrettyPrinter {

    public OCLExpressionsPrettyPrinterConcreteVisitor(IndentPrinter printer) {
        super(printer);
    }

    @Override
    public void handle(ASTOCLQualifiedPrimary node) {
        printer.print(node.toString());
        if (node.isPresentPostfixQualification()) {
            node.getPostfixQualification().accept(getRealThis());
        }
    }

}
