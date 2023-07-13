package de.monticore.bpmn.wf2lts.datastructure;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.lts.LTSBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
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

  /**
   * Create a deep copy of another lts. The new lts is 'isomorphic' to the given one with different states and
   * transitions.
   */
  public LTS(LTS toBeCloned) {
    this(); // Creates a new start state.
    var lookup = new HashMap<State, State>();
    copyStatesAndTransitionsIntoNew(lookup, toBeCloned);
  }

  protected void copyStatesAndTransitionsIntoNew(Map<State, State> lookup, LTS toBeCloned) {
    lookup.put(toBeCloned.getStart(), this.start);
    toBeCloned
        .getEdges()
        .forEach(
            (state, transitions) -> {
              lookup.putIfAbsent(state, new State());
              addState(lookup.get(state));
              transitions.forEach(
                  transition -> {
                    lookup.putIfAbsent(transition.getSource(), new State());
                    lookup.putIfAbsent(transition.getTarget(), new State());
                    addTransition(
                        new Transition(
                            lookup.get(transition.getSource()),
                            transition.getConditions(),
                            transition.getLabel(),
                            lookup.get(transition.getTarget())));
                  });
            });
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
    var toBeRemoved =
        getOutgoingsInPlace(state).stream()
            .filter(transition -> transition.getLabel().equals(label))
            .collect(Collectors.toList());
    transitionMap.get(label).removeAll(toBeRemoved);
    getOutgoingsInPlace(state).removeAll(toBeRemoved);
  }


  public void removeStateIfNoIncomingRecursively(State state) {
    if (!this.getIncoming(state).isEmpty()) {
      return;
    }
    var nextTargets =
        this.getOutgoings(state).stream()
            .map(Transition::getTarget)
            .distinct()
            .collect(Collectors.toList());
    this.removeState(state);
    nextTargets.forEach(this::removeStateIfNoIncomingRecursively);
  }

  public List<State> getStates() {
    return new ArrayList<>(getEdges().keySet());
  }

  protected List<Transition> getOutgoingsAddIfAbsent(State state) {
    if (!getEdges().containsKey(state)) {
      addState(state);
    }
    return getOutgoingsInPlace(state);
  }

  public List<Transition> getOutgoings(State state) {
    return new ArrayList<>(getOutgoingsInPlace(state));
  }

  protected void requireStateIsInLTS(State state) {
    if (!getEdges().containsKey(state)) {
      throw new IllegalArgumentException("State " + state + " is not part of the lts.");
    }
  }

  protected List<Transition> getOutgoingsInPlace(State state) {
    requireStateIsInLTS(state);
    return getEdges().get(state);
  }

  public List<Transition> getIncoming(State state) {
    requireStateIsInLTS(state);
    return transitionMap.values().stream()
        .flatMap(Collection::stream)
        .filter(transition -> transition.getTarget().equals(state))
        .collect(Collectors.toList());
  }

  public List<State> getTerminalStates() {
    return getEdges().keySet().stream()
        .filter(state -> getEdges().get(state).isEmpty())
        .collect(Collectors.toList());
  }

  public void addLTS(LTS otherLTS) {
    // Add all states explicitly. This will also add states that have no incoming transition.
    otherLTS.getEdges().keySet().forEach(this::addState);
    addTransitionsOf(otherLTS);
  }

  public void addTransitionsOf(LTS otherLTS) {
    // Only add transitions -> implicitly adds source and target states.
    // Resulting lts only contains states with at least one incoming or outgoing transition.
    otherLTS.getEdges().forEach((state, transitions) -> transitions.forEach(this::addTransition));
  }

  public boolean isLabelPresent(String label) {
    return transitionMap.containsKey(label) && !transitionMap.get(label).isEmpty();
  }

  public List<String> allUsedLabels() {
    return transitionMap.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .map(Entry::getKey)
        .collect(Collectors.toList());
  }

  public List<Transition> getTransitionsForLabel(String label) {
    // TODO should we throw error if label is not present?
    return new ArrayList<>(transitionMap.getOrDefault(label, new ArrayList<>()));
  }

  public <S, L, B extends LTSBuilder<S, L>> B toModel(B builder) {
    return toModel(builder, getNamingStrategy());
  }

  public <S, L, B extends LTSBuilder<S, L>> B toModel(B builder, NamingStrategy<LTS.State> namingStrategy) {
    Map<State, S> stateLookup = new HashMap<>();
    Map<String, L> labelLookup = new HashMap<>();
    addStatesToBuilder(builder, namingStrategy, stateLookup);
    addFinalStatesToBuilder(builder, namingStrategy, stateLookup);
    this.allUsedLabels().forEach(label ->
        labelLookup.put(label, builder.addLabel(label)));
    addTransitionsToBuilder(builder, stateLookup, labelLookup);

    return builder;
  }

  protected <B extends LTSBuilder<S, L>, S, L> void addStatesToBuilder(
      B builder,
      NamingStrategy<State> namingStrategy,
      Map<State, S> stateLookup) {
    getEdges()
        .keySet()
        .forEach(
            state -> {
              if (state != getStart()) {
                stateLookup.put(state, builder.addState(namingStrategy.apply(state)));
              } else {
                stateLookup.put(state, builder.addInitialState(namingStrategy.apply(state)));
              }
            });
  }

  protected <B extends LTSBuilder<S, L>, S, L> void addTransitionsToBuilder(
      B builder,
      Map<State, S> stateLookup,
      Map<String, L> labelLookup) {
    getEdges().values().stream()
        .flatMap(List::stream)
        .forEach(
            transition ->
                builder.addTransition(
                    stateLookup.get(transition.getSource()),
                    stateLookup.get(transition.getTarget()),
                    labelLookup.get(transition.getLabel())
                    //transition.getConditions() TODO
                )
        );
  }

  protected <B extends LTSBuilder<S, L>, S, L> void addFinalStatesToBuilder(
      B builder,
      NamingStrategy<State> namingStrategy,
      Map<State, S> stateLookup) {
  }


  protected NamingStrategy<LTS.State> getNamingStrategy() {
    return new NamingStrategy<>() {
      private final Map<State, String> lookup;
      private int counter;

      private int nextId() {
        var next = counter;
        counter++;
        return next;
      }

      {
        lookup = LTS.this.getStates()
            .stream()
            .collect(Collectors.toMap(
                    Function.identity(),
                    state -> "s" + nextId()
                )
            );
      }

      @Override
      public String apply(State state) {
        return lookup.get(state);
      }
    };
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

    @Override
    public String toString() {
      return "Transition{"
          + getSource().toString()
          + " --> "
          + getTarget().toString()
          + " : "
          + getLabel()
          + " with "
          + getConditions().toString()
          + '}';
    }
  }
}
