package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class FlowBlockMinTwoTest extends AbstractCoCoTest {
    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getSequenceFlowChecker();
    }

    @Test
    void FlowBlockHasLessThanTwoBranches() {
        String modelName = "de.monticore.bpmn.cocos.flows.invalid.FlowBlockMinTwo";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM3007")),
                        Finding.error(Messages.get("0xWFM3007")));
        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void FlowBlockHasAtLeastTwoBranches() {
        String modelName = "de.monticore.bpmn.cocos.flows.valid.FlowBlockMinTwo";

        testModelNoErrors(modelName);
    }
}
