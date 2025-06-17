package de.monticore.bpmn.cocos.conditions;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class ExpressionOfEventTriggerConditionalIsBooleanTest extends AbstractCoCoTest {
    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getTypesChecker();
    }


    @Test
    void isNotBoolean() {
        String modelName =
                "de.monticore.bpmn.cocos.conditions.invalid.ExpressionOfEventTriggerConditionalIsBoolean";

        Collection<Finding> expectedErrors =
                Lists.newArrayList(
                        Finding.error(Messages.get("0xWFM9007")));

        testModelForErrors(modelName, expectedErrors);
    }


    @Test
    void isBoolean() {
        String modelName =
                "de.monticore.bpmn.cocos.conditions.valid.ExpressionOfEventTriggerConditionalIsBoolean";

        testModelNoErrors(modelName);
    }
}
