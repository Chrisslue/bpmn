package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class ProcessNetIsSoundTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getBehavioralChecker();
    }

    @Test
    void noOptionToComplete() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.NoOptionToComplete";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM8003", "NoOptionToComplete"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Disabled
    @Test
    void unsafe() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.Unsafe";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM8005", "Task2")),
                Finding.warning(Messages.get("0xWFM8005", "End2"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Test
    void safe() {
        String modelName = "de.monticore.bpmn.cocos.analysis.valid.Safe";

        testModelNoErrors(modelName);
    }

    @Test
    void dead() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.DeadNodes";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM8003", "DeadNodes")),
                Finding.warning(Messages.get("0xWFM8004", "Task4"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Test
    void deadlock() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.SyncDeadlockAND";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM8003","SyncDeadlockAND")),
                Finding.warning(Messages.get("0xWFM8004", "_Event_4"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

}
