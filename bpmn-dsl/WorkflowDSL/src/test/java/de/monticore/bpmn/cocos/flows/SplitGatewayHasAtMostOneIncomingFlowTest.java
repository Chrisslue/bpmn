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

class SplitGatewayHasAtMostOneIncomingFlowTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getSequenceFlowChecker(); }
  
  @Test
  void splitHasMultipleIncoming() {
    String modelName = "de.monticore.bpmn.cocos.flows.invalid.SplitGatewayHasAtMostOneIncomingFlow";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM5003",
        "G1")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void mergeHasAtMostOneIncoming() {
    String modelName = "de.monticore.bpmn.cocos.flows.valid.SplitGatewayHasAtMostOneIncomingFlow";
    
    testModelNoErrors(modelName);
  }
  
}
