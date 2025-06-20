/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTFlowBlock;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowBlockCoCo;
import de.se_rwth.commons.logging.Log;

public class FlowBlockMinTwo implements WorkflowASTFlowBlockCoCo {
  
  @Override
  public void check(final ASTFlowBlock flow) {
    if (flow.sizeBranch() < 2) {
      Log.error(Messages.get("0xWFM3007"));
    }
  }
  
}
