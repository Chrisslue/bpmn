/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 174 Description: An Event Sub-Process MUST
 * NOT have any incoming xor outgoing Sequence Flows
 */
public class EventSubProcessHasNoIncomingOrOutgoingFlow implements WorkflowASTWFSubProcessCoCo {
  
  @Override
  public void check(final ASTWFSubProcess subProcess) {
    /*
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
    */
  }
  
}
