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

class StartEventTopLevelProcessHasValidTriggerTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventTriggerChecker();
  }

  @Test
  void invalidTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.invalid.StartEventTopLevelProcessHasValidTrigger";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2010", "E1")),
            Finding.error(Messages.get("0xWFM2010", "E2")),
            Finding.error(Messages.get("0xWFM2010", "E3")),
            Finding.error(Messages.get("0xWFM2010", "E4")),
            Finding.error(Messages.get("0xWFM2010", "E5")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.valid.StartEventTopLevelProcessHasValidTrigger";

    testModelNoErrors(modelName);
  }
}
