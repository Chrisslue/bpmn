/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class ProcessHasNoDeadNodesTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getStructuralChecker(); }
  
  @Test
  void deadNodes() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.DeadNodes2";
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM7009",
        "LoopGateway")), Finding.error(Messages.get("0xWFM7009", "Task2")), Finding.error(Messages
            .get("0xWFM7009", "Wait")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
}
