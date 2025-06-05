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

public class NonInterruptingEventIsSubProcessStartOrBoundaryTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventChecker();
  }

  @Test
  void isNeitherThrowNorCatch() {
    String modelName =
        "de.monticore.bpmn.cocos.events.invalid.NonInterruptingEventIsSubProcessStartOrBoundary";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2018", "E1")),
            Finding.error(Messages.get("0xWFM2018", "E2")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void isThrowOrCatch() {
    String modelName =
        "de.monticore.bpmn.cocos.events.valid.NonInterruptingEventIsSubProcessStartOrBoundary";

    testModelNoErrors(modelName);
  }
}
