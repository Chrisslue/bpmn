package de.monticore.bpmn.workflow._ast;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.stream.Stream;

public class ASTGateway extends ASTGatewayTOP {

  public boolean isConverging() {
    return getDirection() == ASTConstantsWorkflow.MERGE;
  }

  public boolean isDiverging() {
    return getDirection() == ASTConstantsWorkflow.SPLIT;
  }

  public boolean isExclusiveEventBasedInstantiate() {
    return getType().isExclusiveEventBased() && getIncomingsList().isEmpty();
  }

  public boolean isParallelEventBasedInstantiate() {
    return getType().isParallelEventBased() && getIncomingsList().isEmpty();
  }

}
