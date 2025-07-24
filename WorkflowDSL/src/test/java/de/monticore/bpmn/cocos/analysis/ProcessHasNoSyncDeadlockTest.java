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

class ProcessHasNoSyncDeadlockTest extends AbstractCoCoTest {
  
  // ToDo: Re-enable this test when the CoCo is corrected.
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getStructuralChecker(); }
  
  @Disabled
  @Test
  void mergeANDDeadlock() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.SyncDeadlockAND";
    Collection<Finding> expectedWarnings = Lists.newArrayList(Finding.warning(Messages.get(
        "0xWFM7001", "_Gateway_2", "_Gateway_3")));
    
    testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
  }
  
  @Disabled
  @Test
  void mergeIORDeadlock() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.SyncDeadlockIOR";
    Collection<Finding> expectedWarnings = Lists.newArrayList(Finding.warning(Messages.get(
        "0xWFM7001", "_Gateway_2", "_Gateway_3")));
    
    testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
  }
  
}
