package de.monticore.wf2ltl.datastructure;

import de.monticore.bpmn.workflow._ast.ASTFlowCondition;

import java.util.*;
import java.util.stream.Collectors;

public class LTS extends IntermediateGraph<LTS.State, LTS.Transition> {

  private final Map<String, List<Transition>> transitionMap;

  public LTS() {
    this(new State());
  }

  public LTS(State startNode) {
    super(startNode);
    this.transitionMap = new HashMap<>();
  }

  public void addTransition(Transition transition) {
    getOutgoingAddIfAbsent(transition.getSource()).add(transition);
    transitionMap.putIfAbsent(transition.getLabel(), new ArrayList<>());
    transitionMap.get(transition.getLabel()).add(transition);
  }

  public void removeTransition(Transition transition) {
    getEdges().get(transition.getSource()).remove(transition);
    transitionMap.get(transition.getLabel()).remove(transition);
    // TODO Should we remove label if no other transitions exist?
  }

  public List<Transition> getOutgoingAddIfAbsent(State state) {
    edges.putIfAbsent(state, new ArrayList<>());
    return getOutgoings(state);
  }

  public List<Transition> getOutgoings(State state) {
    return Collections.unmodifiableList(edges.get(state));
  }

  public List<Transition> getIncoming(State state) {
    return transitionMap.values()
        .stream()
        .flatMap(Collection::stream)
        .filter(transition -> transition.getTarget().equals(state))
        .collect(Collectors.toList());
  }

  public List<State> getTerminalStates() {
    return getEdges().keySet()
        .stream()
        .filter(state -> getEdges().get(state).isEmpty())
        .collect(Collectors.toList());
  }

  public void addLTS(LTS otherLTS) {
    // States are added implicitly.
    otherLTS.getEdges().forEach((state, transitions) -> transitions.forEach(this::addTransition));
  }

  public boolean isLabelPresent(String label) {
    return transitionMap.containsKey(label);
  }

  public List<Transition> getTransitionsForLabel(String label) {
    // TODO should we throw error
    return transitionMap.getOrDefault(label, new ArrayList<>());
  }

  public static class State {
  }

  public static class Transition extends EdgeTo<State> {

    protected State source;

    protected String label;

    public Transition(State source, List<ASTFlowCondition> conditions, String label, State target) {
      super(conditions, target);
      this.source = source;
      this.label = label;
    }

    public State getSource() {
      return source;
    }

    public String getLabel() {
      return label;
    }

    public Transition changedSource(State source) {
      return new Transition(source, getConditions(), getLabel(), getTarget());
    }

    public Transition changedLabel(String newLabel) {
      return new Transition(getSource(), getConditions(), newLabel, getTarget());
    }

    public Transition changedTarget(State newTarget) {
      return new Transition(getSource(), getConditions(), getLabel(), newTarget);
    }

    public Transition withAddedConditions(List<ASTFlowCondition> extraConditions) {
      var newConditions = new ArrayList<>(getConditions());
      newConditions.addAll(extraConditions);
      return new Transition(getSource(), newConditions, getLabel(), getTarget());
    }

  }

}
