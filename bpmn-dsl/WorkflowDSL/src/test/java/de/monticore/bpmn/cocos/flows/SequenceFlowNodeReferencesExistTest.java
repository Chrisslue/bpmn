 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class SequenceFlowNodeReferencesExistTest extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getSequenceFlowChecker();
  }

  @Test
  void invalidSourcesAndTargets() {
    String modelName = "de.monticore.bpmn.cocos.flows.invalid.SequenceFlowNodeReferencesExist";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(
            Finding.error(Messages.get("0xWFM1004", "T2")),
            Finding.error(Messages.get("0xWFM1004", "T2")));
    testModelForErrors(modelName, expectedErrors, NoSuchElementException.class);
  }

  @Test
  void validSourcesAndTargets() {
    String modelName = "de.monticore.bpmn.cocos.flows.valid.SequenceFlowNodeReferencesExist";

    testModelNoErrors(modelName);
  }
}
