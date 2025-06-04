package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class AdHocSubProcessHasAdHocCharacteristicsTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getActivityChecker();
    }

    @Test
    void hasNoAdHocCharacteristics() {
        String modelName =
                "de.monticore.bpmn.cocos.activities.invalid.AdHocSubProcessHasAdHocCharacteristics";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM4004", "S1")),
                        Finding.error(Messages.get("0xWFM4004", "S2")));

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void hasAdHocCharacteristics() {
        String modelName =
                "de.monticore.bpmn.cocos.activities.valid.AdHocSubProcessHasNoStartAndEndEvent";

        testModelNoErrors(modelName);
    }
}
