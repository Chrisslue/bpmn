package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTGateway;
import java.util.List;
import petrinet._ast.ASTPlace;

public abstract class GatewayModule extends PetriNetModule<ASTGateway> {

  GatewayModule(ASTGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(flowNode, inputPlaces, outputPlaces);
  }
}
