package de.monticore.bpmn.prettyprint;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WorkflowPrettyPrinterTest extends AbstractTest {


  @Test
  @Disabled // RequestHoliday cannot be parsed
  void prettyPrintRequestHoliday() throws IOException {
    String modelName = "de.monticore.bpmn.examples.RequestHoliday.wfm";
    ASTWorkflowCompilationUnit cu = parseModel(modelName);

    // print AST
    String content = WorkflowMill.prettyPrint(cu, true);

    // parse printed AST
    Optional<ASTWorkflowCompilationUnit> printedCu = WorkflowMill.parser().parse_String(content);
    assertTrue(printedCu.isPresent());

    assertTrue(cu.deepEquals(printedCu.get()));
  }
}
