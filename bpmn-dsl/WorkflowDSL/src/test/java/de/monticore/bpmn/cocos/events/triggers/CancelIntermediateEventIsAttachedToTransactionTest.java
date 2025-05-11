 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CancelIntermediateEventIsAttachedToTransactionTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventTriggerChecker();
  }


  @Test
  void invalidTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.invalid.CancelIntermediateEventIsAttachedToTransaction";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2023", "E1")),
            Finding.error(Messages.get("0xWFM2023", "E2")),
            Finding.error(Messages.get("0xWFM2023", "E3")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Disabled
  @Test
  void validTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.valid.CancelIntermediateEventIsAttachedToTransaction";

    testModelNoErrors(modelName);
  }
}
