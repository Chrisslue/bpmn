/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class SplitGatewayHasMultipleOutgoingFlowsTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getSequenceFlowChecker(); }
  
  @Test
  void splitHasZeroOrOneOutgoing() {
    String modelName = "de.monticore.bpmn.cocos.flows.invalid.SplitGatewayHasMultipleOutgoingFlows";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM5001",
        "G1")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void splitHasMultipleOutgoing() {
    String modelName = "de.monticore.bpmn.cocos.flows.valid.SplitGatewayHasMultipleOutgoingFlows";
    
    testModelNoErrors(modelName);
  }
  
}
