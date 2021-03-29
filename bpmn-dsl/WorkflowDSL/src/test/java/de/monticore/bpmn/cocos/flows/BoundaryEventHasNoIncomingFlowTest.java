package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class BoundaryEventHasNoIncomingFlowTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getSequenceFlowChecker();
    }

    @Test
    void boundaryEventHasIncomingFlow() {
        String modelName = "de.monticore.bpmn.cocos.flows.invalid.BoundaryEventHasNoIncomingFlow";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM2005", "E1")),
                Finding.error(Messages.get("0xWFM2005", "E2")),
                Finding.error(Messages.get("0xWFM3003", "T0", "E1")),
                Finding.error(Messages.get("0xWFM3003", "T0", "E2"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void boundaryEventHasNoIncomingFlow() {
        String modelName = "de.monticore.bpmn.cocos.flows.valid.BoundaryEventHasNoIncomingFlow";

        testModelNoErrors(modelName);
    }

}
