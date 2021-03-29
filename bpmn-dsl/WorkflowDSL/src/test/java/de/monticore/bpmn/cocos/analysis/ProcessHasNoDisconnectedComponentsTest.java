package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class ProcessHasNoDisconnectedComponentsTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getStructuralChecker();
    }

    @Test
    void disconnected() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.Disconnected";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM7010", "{Task1, _Event_1, _Event_2}, {Task2, _Event_3, _Event_4}"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Test
    void connected() {
        String modelName = "de.monticore.bpmn.cocos.analysis.valid.Connected";

        testModelNoErrors(modelName);
    }

}