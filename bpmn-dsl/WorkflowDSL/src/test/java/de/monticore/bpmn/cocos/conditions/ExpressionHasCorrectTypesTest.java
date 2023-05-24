package de.monticore.bpmn.cocos.conditions;

import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class ExpressionHasCorrectTypesTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getTypesChecker();
  }

  @Test
  void invalidExpression() {
    String modelName = "de.monticore.bpmn.cocos.conditions.invalid.Expression";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(
                "0xA0241 No SymTypeExpression could be derived for the FieldAccessExpression contract.bla"));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validExpression() {
    String modelName = "de.monticore.bpmn.cocos.conditions.valid.Expression";

    testModelNoErrors(modelName);
  }
}
