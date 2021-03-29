package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTGateway;
import petrinet._ast.ASTPlace;

import java.util.List;

abstract public class GatewayModule extends PetriNetModule<ASTGateway> {

    GatewayModule(ASTGateway flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
        super(flowNode, inputPlaces, outputPlaces);
    }

}
