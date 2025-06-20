/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.lang;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import org.junit.jupiter.api.Test;

class EmptyModelTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getFullChecker(); }
  
  @Test
  void testEmptyModel() {
    final String modelName = "de.monticore.bpmn.lang.EmptyProcess";
    testModelNoErrors(modelName);
  }
  
  @Override
  protected boolean shouldWriteAuxModels() {
    return true;
  }
  
}
