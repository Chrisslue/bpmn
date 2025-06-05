 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.analysis.petrinet.modules;

import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import java.util.List;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public class EventModule extends PetriNetModule<ASTWFEvent> {

  public EventModule(ASTWFEvent event, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
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
