/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ProcessHasNoDisconnectedComponentsTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getStructuralChecker(); }
  
  @Disabled
  @Test
  void disconnected() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.Disconnected";
    Collection<Finding> expectedWarnings = Lists.newArrayList(Finding.warning(Messages.get(
        "0xWFM7010", "{E1, E2, Task1}, {E3, E4, Task2}")));
    
    testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
  }
  
  @Disabled
  @Test
  void connected() {
    String modelName = "de.monticore.bpmn.cocos.analysis.valid.Connected";
    
    testModelNoErrors(modelName);
  }
  
}
