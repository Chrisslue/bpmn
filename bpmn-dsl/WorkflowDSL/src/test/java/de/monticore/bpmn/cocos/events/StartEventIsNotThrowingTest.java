 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class StartEventIsNotThrowingTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventChecker();
  }

  @Test
  void startEventIsThrowing() {
    String modelName = "de.monticore.bpmn.cocos.events.invalid.StartEventIsNotThrowing";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM2001", "E1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void startEventIsNotThrowing() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.StartEventIsNotThrowing";

    testModelNoErrors(modelName);
  }
}
