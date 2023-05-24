package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 244 Description: The conditionExpression
 * attribute for all outgoing Sequence Flows MUST be set to None
 */
public class StartEventOutgoingFlowHasNoCondition implements WorkflowASTEventCoCo {

  @Override
  public void check(final ASTEvent event) {
    if (event.isStart()) {
      event
          .streamOutgoings()
          .map(SequenceFlow::getConditions)
          .flatMap(Collection::stream)
          .filter(ASTFlowCondition::isPresentCondition)
          .forEach(
              condition ->
                  Log.error(
                      Messages.get("0xWFM3002", event.getName()),
                      condition.get_SourcePositionStart(),
                      condition.get_SourcePositionEnd()));
    }
  }
}
