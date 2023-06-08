package de.monticore.ltl;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ltl.LTS2Mermaid.State;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LTS2Mermaid implements LTSBuilder<State, String, Object> {


  private final Set<State> initialStates;
  private final Set<State> states;
  private final Set<Transition> transitions;

  private int counter;

  public LTS2Mermaid() {
    this.states = new HashSet<>();
    this.transitions = new HashSet<>();
    this.initialStates = new HashSet<>();
    this.counter = 0;
  }

  private String getCounterName() {
    var name = "s" + counter;
    counter++;
    return name;
  }

  @Override
  public void addVariable(Object var) {
    throw new UnsupportedOperationException();
  }

  public State addState() {
    return addState(getCounterName());
  }

  @Override
  public State addState(String name) {
    var state = new State(name);
    states.add(state);
    return state;
  }

  @Override
  public State addInitialState(String name) {
    var state = addState(name);
    initialStates.add(state);
    return state;
  }

  public State addInitialState() {
    return addInitialState(getCounterName());
  }

  public void addTransition(State source, State target, String label) {
    addTransition(source, target, label, Collections.emptyList());
  }

  @Override
  public void addTransition(State source, State target, String label, Collection<ASTExpression> conditions) {
    transitions.add(new Transition(source, target, label, conditions));
  }


  public String build() {
    return build("direction LR");
  }

  public String build(String direction) {
    StringBuilder diagram = new StringBuilder().append("stateDiagram-v2\n");
    diagram.append("\t").append(direction).append("\n");
    for (var state : states) {
      // Declare all states with id = toString and their name as description.
      // This allows for spaces in state-names.
      diagram.append("\t")
          .append(state)
          .append(" : ")
          .append(state.name)
          .append("\n");
    }
    for (State initialState : initialStates) {
      diagram.append("\t").append("[*]").append(" --> ").append(initialState).append("\n");
    }
    for (Transition transition : transitions) {
      diagram.append("\t")
          .append(transition.source)
          .append(" --> ")
          .append(transition.target)
          .append(" : ")
          .append(transition.label);
      if (transition.conditions.isEmpty()) {
        diagram.append("\n");
        continue;
      }
      diagram.append(" with ")
          .append(transition.conditions)
          .append("\n");
    }
    return diagram.toString();
  }

  public static class State {

    private String name;

    public State(String name) {
      this.name = name;
    }

  }

  public static class Transition {

    State source;
    State target;
    String label;

    Collection<ASTExpression> conditions;

    public Transition(State source, State target, String label, Collection<ASTExpression> conditions) {
      this.source = source;
      this.target = target;
      this.label = label;
      this.conditions = conditions;
    }
  }
}
