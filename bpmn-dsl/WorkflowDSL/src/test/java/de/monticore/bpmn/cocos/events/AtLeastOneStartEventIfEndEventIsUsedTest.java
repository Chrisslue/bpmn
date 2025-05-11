 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class AtLeastOneStartEventIfEndEventIsUsedTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getEventChecker();
  }

  @Disabled
  @Test
  void noStartEventUsed() {
    String modelName =
        "de.monticore.bpmn.cocos.events.invalid.AtLeastOneStartEventIfEndEventIsUsed";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(
                Messages.get("0xWFM2006", "AtLeastOneStartEventIfEndEventIsUsed", "\"E1\"")),
            Finding.error(Messages.get("0xWFM2006", "S1", "\"E2\"")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void startEventUsed() {
    String modelName = "de.monticore.bpmn.cocos.events.valid.AtLeastOneStartEventIfEndEventIsUsed";

    testModelNoErrors(modelName);
  }
}
