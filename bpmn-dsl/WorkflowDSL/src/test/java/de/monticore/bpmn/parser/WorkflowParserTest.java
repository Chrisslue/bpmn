/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

public class WorkflowParserTest extends AbstractTest {

  @Test
  public void testRequestVacation() throws IOException {
    String modelName = "de.monticore.bpmn.examples.RequestHoliday";
    parseModel(modelName);
  }

  @Disabled("Model must to be adapted to current grammar")
  @Test
  public void testOnlineStore() throws IOException {
    String modelName = "de.monticore.bpmn.examples.OnlineStore";
    parseModel(modelName);
  }

  @Test
  public void testTask() throws IOException {
    String modelName = "de.monticore.bpmn.petrinet.Task";
    parseModel(modelName);
  }

  @Test
  public void testTimer() throws IOException {
    String modelName = "de.monticore.bpmn.timer.TemporalExpressions";
    parseModel(modelName);
  }

  @Test
  public void testXml() throws IOException {
    String modelName = "de.monticore.bpmn.xml.Example";
    parseModel(modelName);
  }

  @Test
  public void testSimpleTask() throws RecognitionException, IOException {
    WorkflowParser parser = WorkflowMill.parser();

    Optional<ASTWFTask> task =
        parser.parseWFTask(
            new StringReader(
                  "user task FillHolidayCardEntry {\n" + "    io {} -> { holidayCard };\n" + "  }"));

    assertFalse(parser.hasErrors());
    assertTrue(task.isPresent());
  }

  @Disabled("Model must to be adapted to current grammar")
  @Test
  public void testStereotypes() {
    String modelName = "de.monticore.bpmn.stereotypes.Stereotypes";
    parseModel(modelName);
  }

  @Test
  public void testReferencModel() {
    String modelName = "de.monticore.bpmn.conformance.ReferenceModel";
    parseModel(modelName);
  }

  @Test
  public void testBachelorThesis() {
    String modelName = "de.monticore.bpmn.conformance.BachelorThesis";
    parseModel(modelName);
  }

  @Test
  public void testExampleModel() {
    String modelName = "de.monticore.bpmn.examples.OrderToDeliveryWorkflow";
    parseModel(modelName);
  }
}
