 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.gateways;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class EventGatewayDoesNotMixMessageEventsAndReceiveTasksTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getGatewayChecker();
  }


  @Test
  void mixedTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayDoesNotMixMessageEventsAndReceiveTasks";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM5008", "G1")),
            Finding.error(Messages.get("0xWFM5008", "G1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void nonMixedTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.valid.EventGatewayDoesNotMixMessageEventsAndReceiveTasks";

    testModelNoErrors(modelName);
  }
}
