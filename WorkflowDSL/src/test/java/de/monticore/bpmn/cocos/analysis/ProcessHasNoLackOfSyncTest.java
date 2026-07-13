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

class ProcessHasNoLackOfSyncTest extends AbstractCoCoTest {
  
  // ToDo: Re-enable this test when the CoCo is corrected.
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getStructuralChecker(); }
  
  @Disabled("CoCo not yet active — ProcessHasNoLackOfSync has a known bug")
  @Test
  void mergeANDLackOfSync() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.LackOfSyncAND";
    Collection<Finding> expectedWarnings = Lists.newArrayList(Finding.warning(Messages.get(
        "0xWFM7002", "_Gateway_2", "_Gateway_3")));
    
    testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
  }
  
  @Disabled("CoCo not yet active — ProcessHasNoLackOfSync has a known bug")
  @Test
  void mergeIORLackOfSync() {
    String modelName = "de.monticore.bpmn.cocos.analysis.invalid.LackOfSyncIOR";
    Collection<Finding> expectedWarnings = Lists.newArrayList(Finding.warning(Messages.get(
        "0xWFM7002", "_Gateway_2", "_Gateway_3")));
    
    testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
  }
  
}
