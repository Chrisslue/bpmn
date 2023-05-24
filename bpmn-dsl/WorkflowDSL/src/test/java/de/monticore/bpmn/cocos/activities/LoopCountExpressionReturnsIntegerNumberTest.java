package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class LoopCountExpressionReturnsIntegerNumberTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getActivityChecker();
  }

  @Test
  void isNonInteger() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.invalid.LoopCountExpressionReturnsIntegerNumber";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM1010", "boolean")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void isInteger() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.valid.LoopCountExpressionReturnsIntegerNumber";

    testModelNoErrors(modelName);
  }
}
