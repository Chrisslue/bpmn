package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 238 Description: If there is an End Event,
 * then there MUST be at least one Start Event
 */
public class AtLeastOneStartEventIfEndEventIsUsed implements WorkflowASTProcessCoCo {
  /* check will not work as there is no ASTFlowElementContainer */
  @Override
  public void check(final ASTProcess container) {
    Collection<ASTEvent> startEvents = WorkflowCollectors.toStartEventsLocal(container);
    Collection<ASTEvent> endEvents = WorkflowCollectors.toEndEventsLocal(container);

    if (endEvents.size() > 0 && startEvents.size() == 0) {
      String formattedEndEvents =
          endEvents.stream()
              .map(event -> "\"" + event.getName() + "\"")
              .collect(Collectors.joining(", "));

      Log.error(
          Messages.get("0xWFM2006", container.getName(), formattedEndEvents),
          container.get_SourcePositionStart(),
          container.get_SourcePositionEnd());
    }
  }
}
