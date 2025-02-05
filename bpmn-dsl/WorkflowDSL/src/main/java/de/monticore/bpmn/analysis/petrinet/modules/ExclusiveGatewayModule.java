package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import java.util.List;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public class ExclusiveGatewayModule extends GatewayModule {

  public ExclusiveGatewayModule(
      ASTWFGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(flowNode, inputPlaces, outputPlaces);

    initModule();
  }

  private void initModule() {
    String name = getFlowNode().getName();

    ASTPlace p = addPlace("p_gw_xor_" + name);

    inputPlaces.forEach(
        pIn -> {
          ASTTransition t = addTransition("t_xor_in_" + name + "_" + random());
          connect(t, p);
          connect(pIn, t);
        });
    outputPlaces.forEach(
        pOut -> {
          ASTTransition t = addTransition("t_xor_out_" + name + "_" + random());
          connect(p, t);
          connect(t, pOut);
        });
  }
}
