/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.workflow._ast.ASTFlowBlock;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowBlockCoCo;

public class AtMostOneDefaultBranch implements WorkflowASTFlowBlockCoCo {
  
  @Override
  public void check(final ASTFlowBlock block) {
    /*
    final List<ASTFlowBranch> skipBranches =
        block.streamBranch().filter(ASTFlowBranch::isDefault).collect(Collectors.toList());
    if (skipBranches.size() > 1) {
      skipBranches.forEach(
          branch -> Log.error(Messages.get("0xWFM3004"), branch.get_SourcePositionStart()));
    }
    */
  }
  
}
