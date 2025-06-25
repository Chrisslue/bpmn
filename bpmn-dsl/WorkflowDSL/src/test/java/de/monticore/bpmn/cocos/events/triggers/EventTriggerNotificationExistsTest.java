package de.monticore.bpmn.cocos.events.triggers;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;

public class EventTriggerNotificationExistsTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventTriggerChecker();
  }
  
  
  @Disabled
  @Test
  void invalidTriggers() {
    String modelName = "de.monticore.bpmn.cocos.events.triggers.invalid.EventTriggerNotificationExists";
    
    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2025"))
        );
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void validTriggers() {
    String modelName = "de.monticore.bpmn.cocos.events.triggers.valid.EventTriggerNotificationExists";
    
    testModelNoErrors(modelName);
  }
}
