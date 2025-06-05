package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class TaskTypeAttributesAreSetTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getActivityChecker();
    }

    @Test
    void hasNoTaskTypeAttributes() {
        String modelName =
                "de.monticore.bpmn.cocos.activities.invalid.TaskTypeAttributesAreSet";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM6002", "T1")),
                        Finding.error(Messages.get("0xWFM6003", "T2")),
                        Finding.error(Messages.get("0xWFM6004", "T3")),
                        Finding.error(Messages.get("0xWFM6005", "T4")),
                        Finding.error(Messages.get("0xWFM6006", "T5")),
                        Finding.error(Messages.get("0xWFM6007", "T6")),
                        Finding.error(Messages.get("0xWFM6008", "T7"))
                        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void hasTaskTypeAttributes() {
        String modelName =
                "de.monticore.bpmn.cocos.activities.valid.TaskTypeAttributesAreSet";

        testModelNoErrors(modelName);
    }
}
