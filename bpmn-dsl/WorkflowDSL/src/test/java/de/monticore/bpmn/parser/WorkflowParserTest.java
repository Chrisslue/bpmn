 /* (c) https://github.com/MontiCore/monticore */ 
/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.trafos.AddSequenceFlowToFlowNodes;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Test;
 
 public class WorkflowParserTest extends AbstractTest {


  @Test
  public void testRequestVacation() throws IOException {
    String modelName = "de.monticore.bpmn.examples.vacation.RequestVacation";
    parseModel(modelName);
  }

  @Test
  public void testRequestHoliday() throws IOException {
    String modelName = "de.monticore.bpmn.examples.vacation.RequestHoliday";
    parseModel(modelName);
  }


  // Model needs to be semantically refined
  @Test
  public void testOnlineStore() throws IOException {
    String modelName = "de.monticore.bpmn.examples.onlineStore.OnlineStore";
    parseModel(modelName);
  }

  @Test
  public void testTask() throws IOException {
    String modelName = "de.monticore.bpmn.petrinet.TaskProcess";
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
                  "user task FillHolidayCardEntry {\n" + "    in {} -> out { holidayCard };\n" + "  }"));

    assertFalse(parser.hasErrors());
    assertTrue(task.isPresent());
  }


  @Test
  public void testStereotypes() {
    String modelName = "de.monticore.bpmn.stereotypes.Stereotypes";
    parseModel(modelName);
  }

  @Test
  public void testExample1Model() {
    String modelName = "de.monticore.bpmn.examples.orderToDelivery.OrderToDeliveryWorkflow";
    ASTWorkflowCompilationUnit ast = parseModel(modelName);

    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    
    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);
    
    WorkflowCoCoChecker checker = WorkflowCoCos.getBasicChecker();
    checker.checkAll(ast);
  }

  @Test
  public void testExample2Model() {
    String modelName = "de.monticore.bpmn.examples.orderToDelivery.CustomerOrder";
    parseModel(modelName);
  }

  @Test
  public void testExample3Model() {
    String modelName = "de.monticore.bpmn.examples.orderToDelivery.Payment";
    parseModel(modelName);
  }

  @Test
  public void testNoKeywordModel1() {
    String modelName = "de.monticore.bpmn.noKeyword.NoKeyword1";
    parseModel(modelName);
  }

  @Test
  public void testNoKeywordModel2() {
    String modelName = "de.monticore.bpmn.noKeyword.NoKeyword2";
    parseModel(modelName);
  }

  @Test
  public void testNoKeywordModel3() {
    String modelName = "de.monticore.bpmn.noKeyword.NoKeyword3";
    parseModel(modelName);
  }

  @Test
  public void testNoKeywordModel4() {
    String modelName = "de.monticore.bpmn.noKeyword.NoKeyword4";
    parseModel(modelName);
  }


}
