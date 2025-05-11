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

class EventGatewayOutgoingFlowHasNoConditionTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getGatewayChecker();
  }

  @Disabled
  @Test
  void sequenceFlowHasCondition() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayOutgoingFlowsHaveNoCondition";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error(Messages.get("0xWFM5006", "G1")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Disabled
  @Test
  void sequenceFlowHasNoCondition() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.valid.EventGatewayOutgoingFlowsHaveNoCondition";

    testModelNoErrors(modelName);
  }
}
