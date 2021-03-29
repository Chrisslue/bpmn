package de.monticore.bpmn.cocos.events;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class StartEventOutgoingFlowHasNoConditionTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getEventChecker();
    }

    @Test
    void outgoingFlowHasCondition() {
        String modelName = "de.monticore.bpmn.cocos.events.invalid.StartEventOutgoingFlowHasNoCondition";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM3002", "E1")),
                Finding.error(Messages.get("0xWFM3002", "E1"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void outgoingFlowHasNoCondition() {
        String modelName = "de.monticore.bpmn.cocos.events.valid.StartEventOutgoingFlowHasNoCondition";

        testModelNoErrors(modelName);
    }

}
