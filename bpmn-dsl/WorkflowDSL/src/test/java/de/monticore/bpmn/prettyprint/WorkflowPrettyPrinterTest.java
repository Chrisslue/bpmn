package de.monticore.bpmn.prettyprint;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._prettyprint.WorkflowFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class WorkflowPrettyPrinterTest extends AbstractCoCoTest {

  @BeforeAll
  public static void init() {
    AbstractCoCoTest.init();
    LogStub.init();
  }

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getFullChecker();
  }

  @Test
  void prettyPrintRequestHoliday() {
    String modelName = "de.monticore.bpmn.examples.RequestHoliday";

    ASTWorkflowCompilationUnit cu = testModelNoErrors(modelName);

    IndentPrinter ppi = new IndentPrinter();

    WorkflowFullPrettyPrinter pp = new WorkflowFullPrettyPrinter(ppi);

    String content = pp.prettyprint(cu);
    // TODO Check actual against expected output.

    Log.info("Pretty printing the parsed Workflow:", WorkflowPrettyPrinterTest.class.getName());
    Log.info(content, WorkflowPrettyPrinterTest.class.getName());
  }
}
