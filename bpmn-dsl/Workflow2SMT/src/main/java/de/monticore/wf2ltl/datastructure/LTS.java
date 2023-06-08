package de.monticore.wf2ltl.datastructure;

import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class LTS extends IntermediateGraph<LTS.State, LTS.Transition> {

  private final Map<String, List<Transition>> transitionMap;

  public LTS() {
    this(new State());
  }

  public LTS(State startNode) {
    super(startNode);
    this.transitionMap = new HashMap<>();
    addState(startNode);
  }

  public void addState(State state) {
    getEdges().putIfAbsent(state, new ArrayList<>());
  }

  public void removeState(State state) {
    if (getStart().equals(state)) {
      throw new IllegalArgumentException("Cant remove start state.");
    }
    var outgoings = new ArrayList<>(getOutgoingsInPlace(state));
    outgoings.forEach(this::removeTransition);
    getEdges().remove(state);
  }

  public void addTransition(Transition transition) {
    if (!getEdges().containsKey(transition.getTarget())) {
      addState(transition.getTarget());
    }
    getOutgoingsAddIfAbsent(transition.getSource()).add(transition);

    transitionMap.putIfAbsent(transition.getLabel(), new ArrayList<>());
    transitionMap.get(transition.getLabel()).add(transition);
  }

  public void removeTransition(Transition transition) {
    if (!transitionMap.containsKey(transition.getLabel())) {
      throw new IllegalArgumentException("Transition is not part of LTS: " + transition);
    }
    getOutgoingsInPlace(transition.getSource()).remove(transition);
    transitionMap.get(transition.getLabel()).remove(transition);
    if (transitionMap.get(transition.getLabel()).isEmpty()) {
      transitionMap.remove(transition.getLabel());
    }
  }

  public void removeAllOutgoingsWith(State state, String label) {
    var toBeRemoved = getOutgoingsInPlace(state)
        .stream()
        .filter(transition -> transition.getLabel().equals(label))
        .collect(Collectors.toList());
    transitionMap.get(label).removeAll(toBeRemoved);
    getOutgoingsInPlace(state).removeAll(toBeRemoved);
  }

  public void removeTargetIfNoIncomings(List<Transition> transitions) {
    transitions.stream()
        .map(Transition::getTarget)
        .filter(state -> this.getIncoming(state).isEmpty())
        .distinct() // We don't want to remove states twice.
        .collect(Collectors.toList()) // Trigger lazy execution before removing.
        .forEach(this::removeState);
  }

  private List<Transition> getOutgoingsAddIfAbsent(State state) {
    if (!getEdges().containsKey(state)) {
      addState(state);
    }
    return getOutgoingsInPlace(state);
  }

  public List<Transition> getOutgoings(State state) {
    return new ArrayList<>(getOutgoingsInPlace(state));
  }

  private void requireStateIsInLTS(State state) {
    if (!getEdges().containsKey(state)) {
      throw new IllegalArgumentException("State " + state + " is not part of the lts.");
    }
  }

  private List<Transition> getOutgoingsInPlace(State state) {
    requireStateIsInLTS(state);
    return getEdges().get(state);
  }

  public List<Transition> getIncoming(State state) {
    requireStateIsInLTS(state);
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
    return transitionMap.containsKey(label) && !transitionMap.get(label).isEmpty();
  }

  public List<String> allUsedLabels() {
    return transitionMap.entrySet()
        .stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  public List<Transition> getTransitionsForLabel(String label) {
    // TODO should we throw error if label is not present?
    return new ArrayList<>(transitionMap.getOrDefault(label, new ArrayList<>()));
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
