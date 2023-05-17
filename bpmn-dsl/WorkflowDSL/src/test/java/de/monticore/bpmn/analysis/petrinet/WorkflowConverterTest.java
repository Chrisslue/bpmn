package de.monticore.bpmn.analysis.petrinet;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WorkflowConverterTest extends AbstractCoCoTest {

  //fixme
  @Disabled
    @Test
    void testTask() {
        String modelName = "de.monticore.bpmn.petrinet.Task";

        testModelNoErrors(modelName);
    }

    @Override
    protected boolean shouldWriteAuxModels() {
        return true;
    }

    //todo: structure check
    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getStructuralChecker();
    }
}
