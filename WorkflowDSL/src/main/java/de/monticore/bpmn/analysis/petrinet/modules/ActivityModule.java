/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.analysis.petrinet.modules;

import static com.google.common.base.Preconditions.checkArgument;

import de.monticore.bpmn.workflow._ast.ASTWFActivity;
import java.util.List;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public class ActivityModule extends PetriNetModule<ASTWFActivity> {
  
  public ActivityModule(ASTWFActivity activity, List<EventModule> boundaryEvents,
      List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    super(activity, inputPlaces, outputPlaces);
    // an activity may have at most one input place, since it is to be activated independently for
    // each incoming token (uncontrolled flow semantics)
    checkArgument(inputPlaces.size() <= 1);
    
    initModule(boundaryEvents);
  }
  
  private void initModule(final List<EventModule> boundaryEvents) {
    String name = getFlowNode().getName();
    
    ASTTransition tStart;
    ASTTransition tEnd;
    
    if (boundaryEvents.isEmpty()) {
      ASTTransition t = addTransition("t_activity_" + name);
      tStart = t;
      tEnd = t;
    }
    else {
      ASTPlace pActive = addPlace("p_activity_" + name + "_active");
      
      tStart = addTransition("t_activity_" + name + "_start");
      tEnd = addTransition("t_activity_" + name + "_end");
      
      connect(tStart, pActive);
      connect(pActive, tEnd);
      
      boundaryEvents.forEach(module -> {
        ASTTransition tEvent = module.getTransitions().get(0);
        
        connect(pActive, tEvent);
        if (module.getFlowNode().isNoninterrupt()) {
          connect(tEvent, pActive);
        }
      });
    }
    
    inputPlaces.forEach(p -> connect(p, tStart));
    outputPlaces.forEach(p -> connect(tEnd, p));
  }
  
}
