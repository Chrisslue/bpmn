package de.monticore.bpmn.wf2lts.datastructure;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.lts.LTSBuilder;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LTSWithFinalStates extends LTS {


  private final Set<State> finalStates;

  public LTSWithFinalStates() {
    super();
    this.finalStates = new HashSet<>();
  }

  public LTSWithFinalStates(State startNode) {
    super(startNode);
    this.finalStates = new HashSet<>();
  }

  public LTSWithFinalStates(Set<State> finalStates) {
    super();
    finalStates.forEach(super::requireStateIsInLTS);
    this.finalStates = finalStates;
  }

  public LTSWithFinalStates(State startNode, Set<State> finalStates) {
    super(startNode);
    finalStates.forEach(super::requireStateIsInLTS);
    this.finalStates = finalStates;
  }

  public LTSWithFinalStates(LTSWithFinalStates toBeCloned) {
    this(toBeCloned, toBeCloned.finalStates);
  }

  public LTSWithFinalStates(LTS toBeCloned, Collection<State> finalStates) {
    super();
    var lookup = new HashMap<State, State>();
    super.copyStatesAndTransitionsIntoNew(lookup, toBeCloned);
    this.finalStates = finalStates.stream().map(lookup::get).collect(Collectors.toSet());
  }

  public static LTSWithFinalStates ofTerminalStates(LTS lts) {
    if (lts instanceof LTSWithFinalStates) {
      Log.warn("Passed LTSWithFinalStates as argument, this will override existing final states.");
    }
    return new LTSWithFinalStates(lts, lts.getTerminalStates());
  }

  public boolean isFinalState(State state) {
    super.requireStateIsInLTS(state);
    return finalStates.contains(state);
  }

  public void addAsFinalState(State state) {
    // Both methods do nothing if present
    super.addState(state);
    this.finalStates.add(state);
  }

  public void unmarkAsFinal(State state) {
    super.requireStateIsInLTS(state);
    this.finalStates.remove(state);
  }

  @Override
  protected <S, L, B extends LTSBuilder<S, L>> void addFinalStatesToBuilder(B builder,
      NamingStrategy<State> namingStrategy, Map<State, S> stateLookup) {
    finalStates.forEach(s -> stateLookup.put(s, builder.addFinalState(namingStrategy.apply(s))));
  }

  @Override
  public void removeState(State state) {
    super.removeState(state);
    finalStates.remove(state); // contains check not necessary
  }

  public void addLTS(LTSWithFinalStates otherLTS) {
    super.addLTS(otherLTS);
    this.finalStates.addAll(otherLTS.finalStates);
  }

  public Set<State> getFinalStates() {
    return finalStates;
  }
}
