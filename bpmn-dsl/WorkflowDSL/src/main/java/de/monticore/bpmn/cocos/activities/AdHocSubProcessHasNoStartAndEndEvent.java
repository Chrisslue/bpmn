package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 181
 * Description: The list of BPMN elements that MUST NOT be used in an Ad-Hoc Sub-Process: Start Event, End Event, Conversations (graphically), Conversation Links (graphically), and Choreography Activities.
 */
public class AdHocSubProcessHasNoStartAndEndEvent implements WorkflowASTSubProcessCoCo {

    @Override
    public void check(final ASTSubProcess subProcess) {
        if (subProcess.isAdHoc()) {
            WorkflowCollectors.toEventsLocal(subProcess).stream()
                    .filter(event -> event.isStart() || event.isEnd())
                    .forEach(event -> Log.error(Messages.get("0xWFM4003", subProcess.getName()),
                            event.get_SourcePositionStart(), event.get_SourcePositionEnd())
                    );

        }
    }

}