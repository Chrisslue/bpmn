 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

class EndEventHasNoOutgoingFlowTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getSequenceFlowChecker();
  }

  @Test
  void hasOutgoingFlow() {
    String modelName = "de.monticore.bpmn.cocos.flows.invalid.EndEventHasNoOutgoingFlow";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM2004", "E2")),
            Finding.error(Messages.get("0xWFM2004", "_Event_2")));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void hasNoOutgoingFlow() {
    String modelName = "de.monticore.bpmn.cocos.flows.valid.EndEventHasNoOutgoingFlow";

    testModelNoErrors(modelName);
  }
}
