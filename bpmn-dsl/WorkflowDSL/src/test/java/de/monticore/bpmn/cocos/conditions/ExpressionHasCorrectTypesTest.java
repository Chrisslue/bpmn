 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.conditions;

import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// todo: reactivate when (de-)serialization of symbol tables is fixed
class ExpressionHasCorrectTypesTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getTypesChecker();
  }

  @Disabled
  @Test
  void invalidExpression() {
    String modelName = "de.monticore.bpmn.cocos.conditions.invalid.Expression";

    String expectedError = "0xF737F";

    testModelForErrorCode(modelName, expectedError);

  }

  @Test
  void validExpression() {
    String modelName = "de.monticore.bpmn.cocos.conditions.valid.Expression";

    testModelNoErrors(modelName);
  }
}
