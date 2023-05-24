package de.monticore.wf2ltl;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import java.util.function.Function;

/**
 * Encapsulate a block of sequences between two matching gateways (split -> ... -> merge). Build the
 * internal graph connecting both gateways. Might include other nested GatewayScopes or
 * SubprocessScopes. When created (using the constructor) the intermediate graph is build
 * immediately.
 */
public class GatewayScope extends GraphBuildingTraverser {

  private final Function<ASTGatewayType, Boolean> gatewayTypeSelector;

  private ASTGateway closingGateway;

  public GatewayScope(WorkflowTraverser traverser, ASTGateway startElement) {
    super(traverser, startElement);
    if (startElement.isConverging()) {
      throw new IllegalArgumentException("Opened a GatewayScope with a merging gateway");
    }
    this.gatewayTypeSelector = gatewayTypeSelectorBuilder(startElement.getType());

    addOutgoingsAsEdges(startElement);
    for (SequenceFlow sequenceFlow : startElement.getOutgoingsList()) {
      sequenceFlow.getTarget().accept(getTraverser());
    }
  }

  public static Function<ASTGatewayType, Boolean> gatewayTypeSelectorBuilder(ASTGatewayType type) {
    // TODO ASTGatewayType should be Enum?
    if (type.isParallel()) {
      return ASTGatewayTypeTOP::isParallel;
    }
    if (type.isInclusive()) {
      return ASTGatewayTypeTOP::isInclusive;
    }
    if (type.isExclusive()) {
      return ASTGatewayTypeTOP::isExclusive;
    }
    if (type.isComplex()) {
      return ASTGatewayTypeTOP::isComplex;
    }
    if (type.isExclusiveEventBased()) {
      return ASTGatewayTypeTOP::isExclusiveEventBased;
    }
    if (type.isParallelEventBased()) {
      return ASTGatewayTypeTOP::isParallelEventBased;
    }
    throw new IllegalArgumentException("Gateway type was unknown" + type);
  }

  public ASTGateway getClosingGateway() {
    // If no matching gateway was found while traversing the ast the closingGateway will not be set.
    // This will only occur with a malformed diagram.
    if (closingGateway == null) {
      throw new IllegalStateException("Closing gateway was not set");
    }
    return closingGateway;
  }

  private void handleDiverging(ASTGateway gateway) {
    GatewayScope nestedScopeCollector = new GatewayScope(getTraverser(), gateway);
    ASTGateway closingGateway = nestedScopeCollector.getClosingGateway();
    getGraph().getGatewayScopes().add(nestedScopeCollector);

    addOutgoingsAsEdges(gateway);
    getTraverser().setWorkflowHandler(this);
    for (SequenceFlow outgoingFlow : closingGateway.getOutgoingsList()) {
      outgoingFlow.accept(getTraverser());
    }
  }

  private void handleGateway(ASTGateway gateway) {
    if (gateway.isDiverging()) {
      handleDiverging(gateway);
    }
    if (gateway.isConverging() && gatewayTypeSelector.apply(gateway.getType())) {
      // We found the matching merge gateway.
      // Closing the gateway scope -> No further traversing..
      this.closingGateway = gateway;
      return;
    } else { // Continue traversing graph.
      addOutgoingsAsEdges(gateway);
      for (SequenceFlow outgoingFlow : gateway.getOutgoingsList()) {
        outgoingFlow.accept(getTraverser());
      }
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
