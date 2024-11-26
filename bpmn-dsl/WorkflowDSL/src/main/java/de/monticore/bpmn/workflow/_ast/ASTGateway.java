package de.monticore.bpmn.workflow._ast;

public class ASTGateway extends ASTGatewayTOP {

  public boolean isConverging() {
    return getDirection() == ASTConstantsWorkflow.MERGE;
  }

  public boolean isDiverging() {
    return getDirection() == ASTConstantsWorkflow.SPLIT;
  }

  public boolean isExclusiveEventBasedInstantiate() {
    return getType().isExclusiveEventBased() && isEmptyIncomings();
  }

  public boolean isParallelEventBasedInstantiate() {
    return getType().isParallelEventBased() && isEmptyIncomings();
  }

}
