package de.monticore.bpmn.timer;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.prettyprint.WorkflowPrettyPrinter;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;


class TemporalExpressionsTest extends AbstractTest {

    @Test
    void printTemporalExpression() {
        String modelName = "de.monticore.bpmn.timer.TemporalExpressions";

        ASTWorkflowCompilationUnit ast = super.loadModel(modelName);

        IndentPrinter ppi = new IndentPrinter();
        WorkflowPrettyPrinter pp = new WorkflowPrettyPrinter(ppi);
        pp.handle(ast);
        String content = ppi.getContent();

        Log.info("AST after parsing:\n\n" + content, TemporalExpressionsTest.class.getSimpleName());
    }

}
