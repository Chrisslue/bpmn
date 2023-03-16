package de.monticore.bpmn.timer;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._prettyprint.WorkflowFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;


class TemporalExpressionsTest extends AbstractTest {

    @Test
    void printTemporalExpression() {
        String modelName = "de.monticore.bpmn.timer.TemporalExpressions";

        ASTWorkflowCompilationUnit ast = super.loadModel(modelName);

        IndentPrinter ppi = new IndentPrinter();
        WorkflowFullPrettyPrinter pp = new WorkflowFullPrettyPrinter(ppi);
        String content = pp.prettyprint(ast);

        Log.info("AST after parsing:\n\n" + content, TemporalExpressionsTest.class.getSimpleName());
    }

}
