package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 174 Description: An Event Sub-Process MUST
 * NOT have any incoming or outgoing Sequence Flows
 */
public class EventSubProcessHasNoIncomingOrOutgoingFlow implements WorkflowASTSubProcessCoCo {

  @Override
  public void check(final ASTSubProcess subProcess) {
    if (subProcess.getSymbol().isTriggeredByEvent()) {
      subProcess
          .streamIncomings()
          .map(SequenceFlow::getSource)
          .forEach(
              source ->
                  Log.error(
                      Messages.get("0xWFM3001", subProcess.getName()),
                      source.get_SourcePositionStart(),
                      source.get_SourcePositionEnd()));
      subProcess
          .streamOutgoings()
          .map(SequenceFlow::getTarget)
          .forEach(
              target ->
                  Log.error(
                      Messages.get("0xWFM3001", subProcess.getName()),
                      target.get_SourcePositionStart(),
                      target.get_SourcePositionEnd()));
    }
  }
}
