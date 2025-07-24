/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.analysis.petrinet.modules;

import com.google.common.collect.Lists;
import de.monticore.bpmn.analysis.petrinet.PetriNetFactory;
import de.monticore.bpmn.analysis.petrinet.PetriNetUtils;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import java.util.List;
import java.util.Random;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

public abstract class PetriNetModule<E extends ASTFlowElement> {
  
  private final Random random = new Random();
  
  private final E flowNode;
  
  protected List<ASTPlace> inputPlaces;
  protected List<ASTPlace> outputPlaces;
  
  protected List<ASTPlace> places = Lists.newArrayList();
  protected List<ASTTransition> transitions = Lists.newArrayList();
  
  PetriNetModule(E flowNode, List<ASTPlace> inputPlaces, List<ASTPlace> outputPlaces) {
    this.flowNode = flowNode;
    this.inputPlaces = inputPlaces;
    this.outputPlaces = outputPlaces;
  }
  
  public E getFlowNode() { return flowNode; }
  
  public List<ASTPlace> getPlaces() { return Lists.newArrayList(places); }
  
  public List<ASTTransition> getTransitions() { return Lists.newArrayList(transitions); }
  
  protected ASTPlace addPlace(final String name) {
    ASTPlace place = PetriNetFactory.createPlace(name);
    places.add(place);
    
    return place;
  }
  
  protected ASTTransition addTransition(final String name) {
    ASTTransition transition = PetriNetFactory.createTransition(name);
    transitions.add(transition);
    
    return transition;
  }
  
  protected void connect(final ASTPlace place, final ASTTransition transition) {
    PetriNetUtils.connect(place, transition);
  }
  
  protected void connect(final ASTTransition transition, final ASTPlace place) {
    PetriNetUtils.connect(transition, place);
  }
  
  protected int random() {
    return random.nextInt() & Integer.MAX_VALUE;
  }
  
}
