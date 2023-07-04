package de.monticore.wf2lts.scopes;

import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._ast.ASTGatewayType;
import de.monticore.bpmn.workflow._ast.ASTInlineGateway;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.wf2lts.GraphBuildingTraverser;
import java.util.Optional;

/**
 * Encapsulate a block of sequences between two matching gateways (split -> ... -> merge). Or paths from split that end
 * in an end event. Build the internal graph connecting both gateways. Might include other nested GatewayScopes or
 * SubprocessScopes. When created (using the constructor) the intermediate graph is build immediately.
 */
public class GatewayScope extends GraphBuildingTraverser {

  public enum GatewayType {
    XOR,
    IOR,
    PARALLEL,
    EVENT_PARALLEL,
    EVENT_XOR,
    COMPLEX;

    public static GatewayType of(ASTGatewayType type) {
      if (type.isParallel()) {
        return PARALLEL;
      }
      if (type.isInclusive()) {
        return IOR;
      }
      if (type.isExclusive()) {
        return XOR;
      }
      if (type.isComplex()) {
        return COMPLEX;
      }
      if (type.isExclusiveEventBased()) {
        return EVENT_XOR;
      }
      if (type.isParallelEventBased()) {
        return EVENT_PARALLEL;
      }
      throw new IllegalArgumentException("Gateway type was unknown" + type);
    }
  }

  private final GatewayType gatewayType;

  private ASTGateway closingGateway;

  public GatewayScope(WorkflowTraverser traverser, ASTGateway startElement) {
    super(traverser, startElement);
    if (startElement.isConverging()) {
      throw new IllegalArgumentException("Opened a GatewayScope with a merging gateway");
    }
    gatewayType = GatewayType.of(startElement.getType());

    addOutgoingsAsEdges(startElement);
    super.traverseOutgoingTargets(startElement);
  }

  public GatewayType getGatewayType() {
    return gatewayType;
  }

  public Optional<ASTGateway> getClosingGateway() {
    // If no matching gateway was found while traversing the ast the closingGateway will not be set.
    // This will only occur with a malformed diagram.
    return Optional.ofNullable(closingGateway);
  }

  private void handleDiverging(ASTGateway gateway) {
    if (this.getGraph().getStart() == gateway) {
      return;
    }
    GatewayScope nestedScopeCollector = new GatewayScope(getTraverser(), gateway);
    getGraph().getGatewayScopes().add(nestedScopeCollector);

    if (nestedScopeCollector.getClosingGateway().isEmpty()) {
      return;
    }
    ASTGateway closingGateway = nestedScopeCollector.getClosingGateway().get();
    addOutgoingsAsEdges(closingGateway);
    getTraverser().setWorkflowHandler(this);
    super.traverseOutgoingTargets(closingGateway);
  }

  private void handleGateway(ASTGateway gateway) {
    if (gateway.isDiverging()) {
      handleDiverging(gateway);
      return;
    }
    if (gateway.isConverging() && gatewayType == GatewayType.of(gateway.getType())) {
      // We found the matching merge gateway.
      // Closing the gateway scope -> No further traversing..

      // There might be multiple paths to the closing gateway.
      // Therefore, this point might be reached multiple times.
      // This is way we have to test for equality too.
      if (this.closingGateway != null && this.closingGateway != gateway) {
        throw new IllegalArgumentException(
            "Closing gateway was already set: " + this.closingGateway.getName()
                + " and was about to be overriden by: " + gateway.getName());
      }
      this.closingGateway = gateway;
    } else { // Continue traversing graph.
      addOutgoingsAsEdges(gateway);
      super.traverseOutgoingTargets(gateway);
    }
  }

  @Override
  public void handle(ASTNamedGateway node) {
    handleGateway(node);
  }

  @Override
  public void handle(ASTInlineGateway node) {
    handleGateway(node);
  }
}
