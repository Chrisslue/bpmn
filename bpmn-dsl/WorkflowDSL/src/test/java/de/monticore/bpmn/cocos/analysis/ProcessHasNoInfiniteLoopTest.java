package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class ProcessHasNoInfiniteLoopTest extends AbstractCoCoTest {

    @Override
    protected WorkflowCoCoChecker getChecker() {
        return WorkflowCoCos.getStructuralChecker();
    }

    @Test
    void infiniteLoop() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.InfiniteLoop";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM7007", "SplitGateway"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

    @Test
    void loopEntryDeadlock() {
        String modelName = "de.monticore.bpmn.cocos.analysis.invalid.LoopEntryDeadlock";
        Collection<Finding> expectedWarnings = Lists.newArrayList(
                Finding.warning(Messages.get("0xWFM7008", "MergeGateway"))
        );

        testModelForErrors(modelName, Lists.newArrayList(), expectedWarnings);
    }

}