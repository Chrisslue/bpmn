package de.monticore.bpmn.analysis.petrinet.modules;

import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import java.util.List;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public class InclusiveGatewayModule extends GatewayModule {

  public InclusiveGatewayModule(
      ASTWFGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(flowNode, inputPlaces, outputPlaces);

    initModule();
  }

  private void initModule() {
    String name = getFlowNode().getName();

    ASTPlace p = addPlace("p_gw_ior_" + name);

    // OR-join
    Sets.powerSet(Sets.newHashSet(inputPlaces)).stream()
        .filter(subset -> !subset.isEmpty())
        .forEach(
            subset -> {
              ASTTransition t = addTransition("t_ior_before_" + name + "_" + random());

              subset.forEach(pIn -> connect(pIn, t));
              connect(t, p);
            });

    // OR-split
    Sets.powerSet(Sets.newHashSet(outputPlaces)).stream()
        .filter(subset -> !subset.isEmpty())
        .forEach(
            subset -> {
              ASTTransition t = addTransition("t_ior_after_" + name + "_" + random());

              subset.forEach(pOut -> connect(t, pOut));
              connect(p, t);
            });
  }
}
