package de.monticore.bpmn.cocos.events.triggers;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class IntermediateCatchEventHasValidTriggerTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getEventTriggerChecker();
    }

    @Test
    void invalidTriggers() {
        String modelName = "de.monticore.bpmn.cocos.events.triggers.invalid.IntermediateCatchEventHasValidTrigger";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM2013", "E1")),
                Finding.error(Messages.get("0xWFM2013", "E2")),
                Finding.error(Messages.get("0xWFM2013", "E3")),
                Finding.error(Messages.get("0xWFM2013", "E4")),
                Finding.error(Messages.get("0xWFM2023", "E4")),
                Finding.error(Messages.get("0xWFM2013", "E5")),
                Finding.error(Messages.get("0xWFM2013", "E6"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void validTriggers() {
        String modelName = "de.monticore.bpmn.cocos.events.triggers.valid.IntermediateCatchEventHasValidTrigger";

        testModelNoErrors(modelName);
    }

}