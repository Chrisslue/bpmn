package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.List;

public class ASTFlowBranch extends ASTFlowBranchTOP {

  public ListMultimap<ASTFlowNode, List<ASTFlowCondition>> asTarget() {
    return getPath(0).asTarget();
  }

  public Collection<ASTFlowNode> asSource() {
    return getPath(sizePath() - 1).asSource();
  }

  public boolean isDefault() {
    return !isEmptyPath()
        && getPathList().get(0).isPresentCondition()
        && getPathList().get(0).getCondition().isDefault();
  }
}
