/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.gateways;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class EventGatewayTargetReceiveTaskHasNoBoundaryEventsTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getGatewayChecker(); }
  
  @Test
  void targetHasBoundaryEvents() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.invalid.EventGatewayTargetReceiveTaskHasNoBoundaryEvents";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM5009",
        "T3")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void targetHasNoBoundaryEvents() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.valid.EventGatewayTargetReceiveTaskHasNoBoundaryEvents";
    
    testModelNoErrors(modelName);
  }
  
}
