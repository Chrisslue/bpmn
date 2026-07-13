/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 237
 * Each flow node may have at most one outgoing default sequence flow.
 */
public class AtMostOneOutgoingFlowIsDefault implements WorkflowASTWFProcessCoCo {
  
  @Override
  public void check(final ASTWFProcess process) {
    WorkflowCollectors.toFlowNodes(process).forEach(node -> {
      long defaultCount = node.streamOutgoings().filter(SequenceFlow::isDefault).count();
      if (defaultCount > 1) {
        Log.error(Messages.get("0xWFM3008", node.getName()), node.get_SourcePositionStart(), node
            .get_SourcePositionEnd());
      }
    });
  }
  
}
