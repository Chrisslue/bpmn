package de.monticore.bpmn.cocos.gateways;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class EventGatewayOutgoingFlowHasNoConditionTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getGatewayChecker();
    }

    @Test
    void sequenceFlowHasCondition() {
        String modelName = "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayOutgoingFlowsHaveNoCondition";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM5006", "G1"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void sequenceFlowHasNoCondition() {
        String modelName = "de.monticore.bpmn.cocos.gateways.valid.EventGatewayOutgoingFlowsHaveNoCondition";

        testModelNoErrors(modelName);
    }

}