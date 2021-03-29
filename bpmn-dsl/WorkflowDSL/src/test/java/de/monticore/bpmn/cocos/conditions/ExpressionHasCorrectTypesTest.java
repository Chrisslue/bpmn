package de.monticore.bpmn.cocos.conditions;

import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class ExpressionHasCorrectTypesTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getTypesChecker();
    }

    @Test
    void invalidExpression() {
        String modelName = "de.monticore.bpmn.cocos.conditions.invalid.Expression";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error("0xOCLI3 Could not resolve field/method/association: bla on Contract at Expression.wfm:<13,9>"),
                Finding.error("0xCET01 Types mismatch on infix expression at Expression.wfm:<13,22> left: Class right: Integer")
                //Finding.error("0xCET01 Types mismatch on infix expression at Expression.wfm:<14,32> left: String right: Integer")
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void validExpression() {
        String modelName = "de.monticore.bpmn.cocos.conditions.valid.Expression";

        testModelNoErrors(modelName);
    }

}
