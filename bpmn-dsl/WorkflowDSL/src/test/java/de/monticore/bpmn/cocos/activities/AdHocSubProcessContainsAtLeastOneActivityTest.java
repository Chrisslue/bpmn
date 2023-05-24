package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class AdHocSubProcessContainsAtLeastOneActivityTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getActivityChecker();
  }

  @Test
  void hasNoActivities() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.invalid.AdHocSubProcessContainsAtLeastOneActivity";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM4002", "S1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void hasActivities() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.valid.AdHocSubProcessContainsAtLeastOneActivity";

    testModelNoErrors(modelName);
  }
}
