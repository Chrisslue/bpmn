package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTEvent;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

import java.util.List;

public class EventModule extends PetriNetModule<ASTEvent> {

    public EventModule(ASTEvent event, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
        super(event, inputPlaces, outputPlaces);

        initModule();
    }

    private void initModule() {
        String name = getFlowNode().getName();

        ASTTransition t = addTransition("t_event_" + name);
        inputPlaces.forEach(p -> connect(p, t));
        outputPlaces.forEach(p -> connect(t, p));
    }


}
