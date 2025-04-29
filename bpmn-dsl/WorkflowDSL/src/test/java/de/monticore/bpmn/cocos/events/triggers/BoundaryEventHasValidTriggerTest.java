 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class BoundaryEventHasValidTriggerTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventTriggerChecker();
  }

  @Test
  void invalidTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.invalid.BoundaryEventHasValidTrigger";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2016", "E1")),
            Finding.error(Messages.get("0xWFM2016", "E2")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validTriggers() {
    String modelName = "de.monticore.bpmn.cocos.events.triggers.valid.BoundaryEventHasValidTrigger";

    testModelNoErrors(modelName);
  }
}
