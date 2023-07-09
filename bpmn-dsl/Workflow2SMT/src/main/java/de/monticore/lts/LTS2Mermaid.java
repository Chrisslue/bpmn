package de.monticore.lts;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LTS2Mermaid implements LTSBuilder<String, String> {

  private final Set<String> initialStates;
  private final Set<String> states;

  private final Map<String, Integer> state2Id;

  private int stateIdCounter;
  private final Set<Transition> transitions;


  public LTS2Mermaid() {
    this.states = new HashSet<>();
    this.state2Id = new HashMap<>();
    this.stateIdCounter = 0;
    this.transitions = new HashSet<>();
    this.initialStates = new HashSet<>();
  }

  private int nextId() {
    var next = stateIdCounter;
    stateIdCounter++;
    return next;
  }

  @Override
  public void addVariable(String varName, ASTMCQualifiedType varType, ASTExpression value) {
    Log.error("Unsupported Operation");
  }

  @Override
  public String addLabel(String label) {
    return label;
  }

  @Override
  public String addState(String name) {
    states.add(name);
    state2Id.put(name, nextId());
    return name;
  }

  @Override
  public String addFinalState(String name) {
    return addState(name);
  }

  @Override
  public String addInitialState(String name) {
    var state = addState(name);
    initialStates.add(state);
    return state;
  }

  private void addStateIfAbsent(String state) {
    if (!states.contains(state)) {
      addState(state);
    }
  }

  @Override
  public void addTransition(String source, String target, String label) {
    addTransition(source, target, label, Collections.emptyList());
  }

  @Override
  public void addTransition(String source, String target, String label, ASTExpression condition) {
    addTransition(source, target, label, List.of(condition));
  }

  private void addTransition(String source, String target, String label, List<ASTExpression> condition) {
    addStateIfAbsent(source);
    addStateIfAbsent(target);

    transitions.add(new Transition(source, target, label, condition));
  }

  public String build() {
    return build("direction LR");
  }

  public String build(String direction) {
    StringBuilder diagram = new StringBuilder().append("stateDiagram-v2\n");
    diagram.append("\t").append(direction).append("\n");
    for (var state : states) {
      // Declare all states with id = state and their name as description.
      // This allows for spaces in state-names.
      diagram.append("\t").append(state2Id.get(state)).append(" : ").append(state).append("\n");
    }
    for (String initialState : initialStates) {
      diagram.append("\t").append("[*]").append(" --> ").append(state2Id.get(initialState)).append("\n");
    }
    for (Transition transition : transitions) {
      diagram
          .append("\t")
          .append(state2Id.get(transition.source))
          .append(" --> ")
          .append(state2Id.get(transition.target))
          .append(" : ")
          .append(transition.label);
      if (transition.conditions.isEmpty()) {
        diagram.append("\n");
        continue;
      }
      diagram.append(" with ").append(transition.conditions).append("\n");
    }
    return diagram.toString();
  }

  public static class Transition {

    String source;
    String target;
    String label;

    Collection<ASTExpression> conditions;

    public Transition(
        String source, String target, String label, Collection<ASTExpression> conditions) {
      this.source = source;
      this.target = target;
      this.label = label;
      this.conditions = conditions;
    }
  }
}
