/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFActivity;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._ast.ASTWFInlineGateway;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 150, 435, 436, 438
 * Only activities and exclusive, inclusive, or complex gateways may have a default sequence flow.
 */
public class DefaultFlowHasValidSource implements WorkflowASTWFProcessCoCo {
  
  @Override
  public void check(final ASTWFProcess process) {
    WorkflowCollectors.toSequenceFlow(process).stream().filter(SequenceFlow::isDefault).forEach(
        flow -> {
          ASTFlowElement source = flow.getSource();
          if (!isValidDefaultFlowSource(source)) {
            Log.error(Messages.get("0xWFM3006", source.getName()), source.get_SourcePositionStart(),
                source.get_SourcePositionEnd());
          }
        });
  }
  
  private boolean isValidDefaultFlowSource(final ASTFlowElement source) {
    if (source instanceof ASTWFActivity) {
      return true;
    }
    if (source instanceof ASTWFGateway) {
      ASTWFGateway gateway = (ASTWFGateway) source;
      return gateway.getType().isExclusive() || gateway.getType().isInclusive() || gateway.getType()
          .isComplex();
    }
    if (source instanceof ASTWFInlineGateway) {
      ASTWFInlineGateway gateway = (ASTWFInlineGateway) source;
      return gateway.getType().isExclusive() || gateway.getType().isInclusive() || gateway.getType()
          .isComplex();
    }
    return false;
  }
  
}
