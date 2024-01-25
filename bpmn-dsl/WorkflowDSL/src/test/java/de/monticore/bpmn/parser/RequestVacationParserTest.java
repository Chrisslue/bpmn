/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class RequestVacationParserTest extends AbstractTest {

  @Test
  @Disabled // RequestHoliday cannot be parsed
  public void testRequestVacation() throws IOException {
    String modelName = "de.monticore.bpmn.examples.RequestHoliday.wfm";
    parseModel(modelName);
  }

  @Test
  public void testTask() throws RecognitionException, IOException {
    WorkflowParser parser = WorkflowMill.parser();

    Optional<ASTTask> task =
        parser.parseTask(
            new StringReader(
                "task user FillHolidayCardEntry {\n" + "    io: {} -> { holidayCard };\n" + "  }"));

    assertFalse(parser.hasErrors());
    assertTrue(task.isPresent());
  }
}
