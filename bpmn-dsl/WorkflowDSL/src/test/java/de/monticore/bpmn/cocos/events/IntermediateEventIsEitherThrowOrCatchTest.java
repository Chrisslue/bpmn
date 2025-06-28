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

class IntermediateEventIsEitherThrowOrCatchTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getEventChecker(); }
  
  @Test
  void isNeitherThrowNorCatch() {
    String modelName =
        "de.monticore.bpmn.cocos.events.invalid.IntermediateEventIsEitherThrowOrCatch";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM2017",
        "E1")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void isThrowOrCatch() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.IntermediateEventIsEitherThrowOrCatch";
    
    testModelNoErrors(modelName);
  }
  
}
