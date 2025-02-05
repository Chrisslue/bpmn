package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import java.util.List;
import petrinet._ast.ASTPlace;

public abstract class GatewayModule extends PetriNetModule<ASTWFGateway> {

  GatewayModule(ASTWFGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(flowNode, inputPlaces, outputPlaces);
  }
}
