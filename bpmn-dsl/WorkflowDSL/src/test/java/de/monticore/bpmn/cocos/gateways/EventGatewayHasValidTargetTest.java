package de.monticore.bpmn.cocos.gateways;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class EventGatewayHasValidTargetTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getGatewayChecker();
    }

    @Test
    void targetHasInvalidTrigger() {
        String modelName = "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayHasValidTarget";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM5007", "E1", "G1")),
                Finding.error(Messages.get("0xWFM5007", "E2", "G1")),
                Finding.error(Messages.get("0xWFM5007", "E3", "G1")),
                Finding.error(Messages.get("0xWFM5007", "E4", "G1")),
                Finding.error(Messages.get("0xWFM5007", "E5", "G1")),
                Finding.error(Messages.get("0xWFM5007", "E6", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T2", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T3", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T4", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T5", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T6", "G1")),
                Finding.error(Messages.get("0xWFM5007", "T7", "G1")),
                Finding.error(Messages.get("0xWFM5007", "G2", "G1")),
                Finding.error(Messages.get("0xWFM5007", "S1", "G1"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void targetHasValidTrigger() {
        String modelName = "de.monticore.bpmn.cocos.gateways.valid.EventGatewayHasValidTarget";

        testModelNoErrors(modelName);
    }
}