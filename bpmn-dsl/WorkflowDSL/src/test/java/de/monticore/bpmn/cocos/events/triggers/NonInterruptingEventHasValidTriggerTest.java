package de.monticore.bpmn.cocos.events.triggers;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class NonInterruptingEventHasValidTriggerTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getEventTriggerChecker();
    }

    @Test
    void isNonInterrupting() {
        String modelName = "de.monticore.bpmn.cocos.events.triggers.invalid.NonInterruptingEventHasValidTrigger";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM2019", "E01")),
                Finding.error(Messages.get("0xWFM2019", "E02")),
                Finding.error(Messages.get("0xWFM2019", "E11")),
                Finding.error(Messages.get("0xWFM2019", "E12")),
                Finding.error(Messages.get("0xWFM2019", "E13"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void isInterrupting() {
        String modelName = "de.monticore.bpmn.cocos.events.triggers.valid.NonInterruptingEventHasValidTrigger";

        testModelNoErrors(modelName);
    }

}