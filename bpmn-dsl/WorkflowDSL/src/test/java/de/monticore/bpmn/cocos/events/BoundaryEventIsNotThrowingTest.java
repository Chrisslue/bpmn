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

class BoundaryEventIsNotThrowingTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getEventChecker(); }
  
  @Test
  void isThrowing() {
    String modelName = "de.monticore.bpmn.cocos.events.invalid.BoundaryEventIsNotThrowing";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM2015",
        "E1")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void isNotThrowing() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.BoundaryEventIsNotThrowing";
    
    testModelNoErrors(modelName);
  }
  
}
