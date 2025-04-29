 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import static com.google.common.base.Preconditions.checkArgument;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEventTriggerCompensate;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventTriggerCompensateCoCo;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.se_rwth.commons.logging.Log;

public class CompensatedActivityExists implements WorkflowASTWFEventTriggerCompensateCoCo {

  @Override
  public void check(ASTWFEventTriggerCompensate node) {
    /*
    checkArgument(node.getEnclosingScope() != null);

    if (node.isPresentActivity()) {
      WorkflowScope enclosingScope = (WorkflowScope) node.getEnclosingScope();

      if (!enclosingScope.resolveActivityLocally(node.getActivity()).isPresent()) {
        Log.error(
            Messages.get("0xWFM1005", node.getActivity()),
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd());
      }
    }
    */
  }
  
}
