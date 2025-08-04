/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.prettyprint;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class TimerConditionPrettyPrinterTest extends AbstractTest {
  
  @Test
  public void testPrintAndParse() {
    String modelName = "de.monticore.bpmn.prettyprint.TimeAndDate";
    ASTWorkflowCompilationUnit model = loadModel(modelName);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    try {
      Optional<ASTWorkflowCompilationUnit> opt = WorkflowMill.parser().parse_String(WorkflowMill
          .prettyPrint(model, false));
      Assertions.assertTrue(opt.isPresent());
      Assertions.assertTrue(opt.get().deepEquals(model));
    }
    catch (IOException e) {
      Assertions.fail(e.getMessage());
    }
  }
  
}
