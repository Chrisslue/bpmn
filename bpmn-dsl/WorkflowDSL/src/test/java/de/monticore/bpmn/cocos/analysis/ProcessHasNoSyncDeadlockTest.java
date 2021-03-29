package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class ProcessHasNoSyncDeadlockTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getStructuralChecker();
    }

    @Test
    void mergeANDDeadlock() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.SyncDeadlockAND";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM7001", "_Gateway_2", "_Gateway_3"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Test
    void mergeIORDeadlock() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.SyncDeadlockIOR";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM7001", "_Gateway_2", "_Gateway_3"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

}