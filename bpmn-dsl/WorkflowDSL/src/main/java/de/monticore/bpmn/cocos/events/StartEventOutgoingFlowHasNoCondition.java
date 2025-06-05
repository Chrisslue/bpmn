 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 244 Description: The conditionExpression
 * attribute for all outgoing Sequence Flows MUST be set to None
 */
public class StartEventOutgoingFlowHasNoCondition implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent event) {
    if (event.isStart()) {
      event
          .streamOutgoings()
          .map(SequenceFlow::getConditions)
          .flatMap(Collection::stream)
          .filter(ASTFlowCondition::isPresentExpression)
          .forEach(
              condition ->
                  Log.error(
                      Messages.get("0xWFM3002", event.getName()),
                      condition.get_SourcePositionStart(),
                      condition.get_SourcePositionEnd()));
    }
  }
}
