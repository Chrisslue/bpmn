 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EventSubProcessHasOnlyOneStartEventTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getActivityChecker();
  }


  @Test
  void invalidSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.invalid.EventSubProcessHasOnlyOneStartEvent";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM4001", "S1")),
            Finding.error(Messages.get("0xWFM4001", "S1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.valid.EventSubProcessHasOnlyOneStartEvent";

    testModelNoErrors(modelName);
  }
}
