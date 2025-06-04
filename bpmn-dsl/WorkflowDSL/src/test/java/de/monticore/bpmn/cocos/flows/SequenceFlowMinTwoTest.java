package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.NoSuchElementException;

public class SequenceFlowMinTwoTest extends AbstractCoCoTest {
    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getSequenceFlowChecker();
    }

    @Test
    void SequenceFlowHasLessThanTwoElements() {
        String modelName = "de.monticore.bpmn.cocos.flows.invalid.SequenceFlowMinTwo";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM3006")),
                        Finding.error(Messages.get("0xWFM3006")),
                        Finding.error(Messages.get("0xWFM3006" )));
        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void SequenceFlowHasAtLeastTwoElements() {
        String modelName = "de.monticore.bpmn.cocos.flows.valid.SequenceFlowMinTwo";

        testModelNoErrors(modelName);
    }
}
