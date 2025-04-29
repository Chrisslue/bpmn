 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.analysis.petrinet;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import org.junit.jupiter.api.Test;

class WorkflowConverterTest extends AbstractCoCoTest {

  @Test
  void testTask() {
    String modelName = "de.monticore.bpmn.petrinet.Task";

    testModelNoErrors(modelName, 1);
  }

  @Override
  protected boolean shouldWriteAuxModels() {
    return true;
  }

  // todo: structure check
  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getStructuralChecker();
  }
}
