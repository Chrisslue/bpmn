/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class AtLeastOneEndEventIfStartEventIsUsedTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getEventChecker(); }
  
  //ToDo: Subprocess needs to be checked separately
  @Test
  void noEndEventUsed() {
    String modelName =
        "de.monticore.bpmn.cocos.events.invalid.AtLeastOneEndEventIfStartEventIsUsed";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM2007",
        "AtLeastOneEndEventIfStartEventIsUsed", "\"E1\"")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void endEventUsed() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.AtLeastOneEndEventIfStartEventIsUsed";
    
    testModelNoErrors(modelName);
  }
  
}
