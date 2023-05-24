package de.monticore.ltl;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.Collection;

public interface LTSBuilder<State, Label, Variable> {

  void addVariable(Variable var);

  State addState(String name);

  State addInitialState(String name);

  void addTransition(State source, State target, Label label, Collection<ASTExpression> conditions);
}


