/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.conditions;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class MILoopCompletionConditionIsBooleanTest extends AbstractCoCoTest {
    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getTypesChecker();
    }

    @Test
    void isNotBoolean() {
        String modelName =
                "de.monticore.bpmn.cocos.conditions.invalid.MILoopCompletionConditionIsBoolean";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM9003")));

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void isBoolean() {
        String modelName =
                "de.monticore.bpmn.cocos.conditions.valid.MILoopCompletionConditionIsBoolean";

        testModelNoErrors(modelName);
    }
}
