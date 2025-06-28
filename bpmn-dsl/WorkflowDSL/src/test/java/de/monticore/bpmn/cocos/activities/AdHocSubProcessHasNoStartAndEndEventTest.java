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

class AdHocSubProcessHasNoStartAndEndEventTest extends AbstractCoCoTest {
  
  @Override
  protected WorkflowCoCoChecker getChecker() { return WorkflowCoCos.getActivityChecker(); }
  
  @Test
  void hasStartOrEndEvents() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.invalid.AdHocSubProcessHasNoStartAndEndEvent";
    
    Collection<Finding> expectedErrors = Lists.newArrayList(Finding.error(Messages.get("0xWFM4003",
        "S1")), Finding.error(Messages.get("0xWFM4003", "S2")), Finding.error(Messages.get(
            "0xWFM4003", "S3")), Finding.error(Messages.get("0xWFM4003", "S3")));
    
    testModelForErrors(modelName, expectedErrors);
  }
  
  @Test
  void hasNoStartOrEndEvent() {
    String modelName =
        "de.monticore.bpmn.cocos.activities.valid.AdHocSubProcessHasNoStartAndEndEvent";
    
    testModelNoErrors(modelName);
  }
  
}
