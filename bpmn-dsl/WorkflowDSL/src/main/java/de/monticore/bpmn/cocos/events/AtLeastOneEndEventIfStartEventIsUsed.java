package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowElementContainerCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 246
 * Description: If there is a Start Event, then there MUST be at least one End Event.
 */

public class AtLeastOneEndEventIfStartEventIsUsed implements WorkflowASTFlowElementContainerCoCo {

    @Override
    public void check(final ASTFlowElementContainer container) {
        Collection<ASTEvent> startEvents = WorkflowCollectors.toStartEventsLocal(container);
        Collection<ASTEvent> endEvents = WorkflowCollectors.toEndEventsLocal(container);

        if (startEvents.size() > 0 && endEvents.size() == 0) {
            String formattedStartEvents = startEvents.stream()
                    .map(event -> "\"" + event.getName() + "\"")
                    .collect(Collectors.joining(", "));

            Log.error(Messages.get("0xWFM2007", container.getName(), formattedStartEvents),
                    container.get_SourcePositionStart(), container.get_SourcePositionEnd()
            );
        }
    }

}
