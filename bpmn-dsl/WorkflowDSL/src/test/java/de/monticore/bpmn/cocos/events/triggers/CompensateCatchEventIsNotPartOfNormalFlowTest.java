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

public class CompensateCatchEventIsNotPartOfNormalFlowTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventTriggerChecker();
  }

  @Test
  void invalidTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.invalid.CompensateBoundaryEventHasNoOutgoingSequenceFlow";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM2024", "E3")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validTriggers() {
    String modelName =
        "de.monticore.bpmn.cocos.events.triggers.valid.CompensateBoundaryEventHasNoOutgoingSequenceFlow";

    testModelNoErrors(modelName);
  }
}
