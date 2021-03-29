package de.monticore.bpmn.cocos.gateways;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class EventGatewayTargetHasNoAdditionalIncomingFlowTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getGatewayChecker();
    }

    @Test
    void targetHasAdditionalIncomingFlow() {
        String modelName = "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayTargetHasNoAdditionalIncomingFlow";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM5011", "T3")),
                Finding.error(Messages.get("0xWFM5011", "T3"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void targetHasNoAdditionalIncomingFlow() {
        String modelName = "de.monticore.bpmn.cocos.gateways.valid.EventGatewayTargetHasNoAdditionalIncomingFlow";

        testModelNoErrors(modelName);
    }
}