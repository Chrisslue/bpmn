package de.monticore.bpmn.cocos.events;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class EndEventIsNotCatchingTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventChecker();
  }

  @Test
  void endEventIsCatching() {
    String modelName = "de.monticore.bpmn.cocos.events.invalid.EndEventIsNotCatching";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM2002", "E2")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void endEventIsNotCatching() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.EndEventIsNotCatching";

    testModelNoErrors(modelName);
  }
}
