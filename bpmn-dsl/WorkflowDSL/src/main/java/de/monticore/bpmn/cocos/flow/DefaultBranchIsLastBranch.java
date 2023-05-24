package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTFlowBlock;
import de.monticore.bpmn.workflow._ast.ASTFlowBranch;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowBlockCoCo;
import de.se_rwth.commons.logging.Log;
import one.util.streamex.EntryStream;

public class DefaultBranchIsLastBranch implements WorkflowASTFlowBlockCoCo {

  @Override
  public void check(final ASTFlowBlock block) {
    EntryStream.of(block.getBranchList())
        .filterKeys(index -> index < block.sizeBranch())
        .filterValues(ASTFlowBranch::isDefault)
        .values()
        .forEach(branch -> Log.error(Messages.get("0xWFM3005"), branch.get_SourcePositionStart()));
  }
}
