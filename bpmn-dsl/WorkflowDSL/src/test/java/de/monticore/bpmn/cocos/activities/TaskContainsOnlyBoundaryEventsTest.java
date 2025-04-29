 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

public class TaskContainsOnlyBoundaryEventsTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getActivityChecker();
  }

  @Test
  void taskContainsNonBoundaryEvents() {
    String modelName = "de.monticore.bpmn.cocos.activities.invalid.TaskContainsOnlyBoundaryEvents";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM1007", "T1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void taskContainsOnlyBoundaryEvents() {
    String modelName = "de.monticore.bpmn.cocos.activities.valid.TaskContainsOnlyBoundaryEvents";

    testModelNoErrors(modelName);
  }
}
