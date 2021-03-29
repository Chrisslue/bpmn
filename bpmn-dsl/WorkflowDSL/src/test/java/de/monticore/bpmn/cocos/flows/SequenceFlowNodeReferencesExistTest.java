package de.monticore.bpmn.cocos.flows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class SequenceFlowNodeReferencesExistTest extends AbstractCoCoTest {

    @BeforeAll
    public static void init() {
        Log.enableFailQuick(true);
    }

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getSequenceFlowChecker();
    }

    @Disabled
    @Test
    void invalidSourcesAndTargets() {
        String modelName = "de.monticore.bpmn.cocos.flows.invalid.SequenceFlowNodeReferencesExist";

        Collection<Finding> expectedErrors = Lists.newArrayList(
                Finding.error(Messages.get("0xWFM1004", "T2"))
        );

        testModelForErrors(modelName, expectedErrors);
    }

    @Test
    void validSourcesAndTargets() {
        String modelName = "de.monticore.bpmn.cocos.flows.valid.SequenceFlowNodeReferencesExist";

        testModelNoErrors(modelName);
    }

}
