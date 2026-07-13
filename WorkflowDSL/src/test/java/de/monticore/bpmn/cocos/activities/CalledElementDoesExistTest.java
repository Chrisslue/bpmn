/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.activities;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class CalledElementDoesExistTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getTypesChecker(); }
  
  @Test
  void calledElementMissing() {
    String modelName = "de.monticore.bpmn.cocos.activities.invalid.CalledElementDoesExist";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM1008",
        "NonExistentProcess")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void calledElementExists() {
    String modelName = "de.monticore.bpmn.cocos.activities.valid.CalledElementDoesExist";
    
    testModelNoErrors(modelName);
  }
  
}
