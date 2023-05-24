package de.monticore.bpmn.workflow._ast;

public interface ASTGateway extends ASTGatewayTOP {

  default boolean isConverging() {
    return getDirection() == ASTGatewayDirection.MERGE;
  }

  default boolean isDiverging() {
    return getDirection() == ASTGatewayDirection.SPLIT;
  }

  String getName();

  default boolean isExclusiveEventBasedInstantiate() {
    return getType().isExclusiveEventBased() && isEmptyIncomings();
  }

  default boolean isParallelEventBasedInstantiate() {
    return getType().isParallelEventBased() && isEmptyIncomings();
  }
}
