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

class BoundaryEventIsContainedByActivityTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventChecker();
  }

  @Test
  void boundaryEventIsNotContainedByActivity() {
    String modelName = "de.monticore.bpmn.cocos.events.invalid.BoundaryEventIsContainedByActivity";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM1006", "E1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void boundaryEventIsContainedByActivity() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.BoundaryEventIsContainedByActivity";

    testModelNoErrors(modelName);
  }
}
