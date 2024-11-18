package de.monticore.bpmn.workflow._ast;

public interface ASTGateway extends ASTGatewayTOP {

  public boolean isConverging() {
    return getDirection() == ASTConstantsWorkflow.MERGE;
  }

  public boolean isDiverging() {
    return getDirection() == ASTConstantsWorkflow.SPLIT;
  }
}
