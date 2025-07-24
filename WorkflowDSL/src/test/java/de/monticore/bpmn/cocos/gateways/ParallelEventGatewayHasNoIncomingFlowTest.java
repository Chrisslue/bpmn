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

class ParallelEventGatewayHasNoIncomingFlowTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getGatewayChecker(); }
  
  @Test
  void eventGatewayHasIncomingFlow() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.invalid.ParallelEventGatewayHasNoIncomingFlow";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM5012",
        "G1")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void eventGatewayHasNoIncomingFlow() {
    String modelName =
        "de.monticore.bpmn.cocos.gateways.valid.ParallelEventGatewayHasNoIncomingFlow";
    
    testModelNoErrors(modelName);
  }
  
}
