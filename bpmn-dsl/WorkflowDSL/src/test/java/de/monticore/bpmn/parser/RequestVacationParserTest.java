/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.parser;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Disabled
public class RequestVacationParserTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getFullChecker();
    }

    @Test
    public void testRequestVacation() {
        String modelName = "de.monticore.bpmn.examples.vacation.RequestVacation";

        testModelNoErrors(modelName);
    }

    @Test
    public void testTask() throws RecognitionException, IOException {
        WorkflowParser parser = new WorkflowParser();

        Optional<ASTTask> task = parser.parseTask(new StringReader("task user FillHolidayCardEntry {\n" +
                "    io: {} -> { holidayCard };\n" +
                "  }"));

        assertFalse(parser.hasErrors());
        assertTrue(task.isPresent());
    }

}
