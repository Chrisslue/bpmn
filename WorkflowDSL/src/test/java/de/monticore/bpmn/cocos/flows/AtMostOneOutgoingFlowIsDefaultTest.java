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

class AtMostOneOutgoingFlowIsDefaultTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getSequenceFlowChecker(); }
  
  @Test
  void multipleDefaultFlows() {
    String modelName = "de.monticore.bpmn.cocos.flows.invalid.AtMostOneOutgoingFlowIsDefault";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM3008",
        "Gateway")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void atMostOneDefaultFlow() {
    String modelName = "de.monticore.bpmn.cocos.flows.valid.AtMostOneOutgoingFlowIsDefault";
    
    testModelNoErrors(modelName);
  }
  
}
