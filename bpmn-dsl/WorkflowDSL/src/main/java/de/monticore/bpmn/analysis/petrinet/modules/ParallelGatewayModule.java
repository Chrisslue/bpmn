package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTGateway;
import java.util.List;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public class ParallelGatewayModule extends GatewayModule {

  public ParallelGatewayModule(
      ASTGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(flowNode, inputPlaces, outputPlaces);

    initModule();
  }

  private void initModule() {
    ASTTransition t = addTransition("t_gw_and_" + getFlowNode().getName());

    inputPlaces.forEach(pIn -> connect(pIn, t));
    outputPlaces.forEach(pOut -> connect(t, pOut));
  }
}
