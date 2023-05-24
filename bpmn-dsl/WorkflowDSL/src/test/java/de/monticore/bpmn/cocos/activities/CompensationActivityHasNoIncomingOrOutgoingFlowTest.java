package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class CompensationActivityHasNoIncomingOrOutgoingFlowTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getActivityChecker();
  }

  @Test
  void invalidSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.invalid.CompensationActivityHasNoIncomingOrOutgoingFlow";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM6001", "T2")),
            Finding.error(Messages.get("0xWFM6001", "T3")),
            Finding.error(Messages.get("0xWFM6001", "T3")),
            Finding.error(Messages.get("0xWFM6001", "S1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.valid.CompensationActivityHasNoIncomingOrOutgoingFlow";

    testModelNoErrors(modelName);
  }
}
