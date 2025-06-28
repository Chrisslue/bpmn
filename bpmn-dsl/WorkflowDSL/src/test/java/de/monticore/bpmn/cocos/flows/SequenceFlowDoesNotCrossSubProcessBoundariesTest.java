 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SequenceFlowDoesNotCrossSubProcessBoundariesTest extends AbstractCoCoTest {
  // ToDo: Re-enable this test when the CoCo is corrected.
  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getSequenceFlowChecker();
  }

  @Disabled
  @Test
  void invalidSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.flows.invalid.SequenceFlowDoesNotCrossSubProcessBoundaries";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM1004",  "T02")),
            Finding.error(Messages.get("0xWFM3003", "E11", "T11")));
    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void validSourcesAndTargets() {
    String modelName =
        "de.monticore.bpmn.cocos.flows.valid.SequenceFlowDoesNotCrossSubProcessBoundaries";

    testModelNoErrors(modelName);
  }
}
