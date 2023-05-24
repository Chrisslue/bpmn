package de.monticore.lts;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

public interface LTSBuilder<State, Label> {

  void addVariable(String varName, ASTMCQualifiedType varType, ASTExpression value);

  Label addLabel(String label);

  State addState(String name);

  State addInitialState(String name);

  State addFinalState(String name);

  void addTransition(State source, State target, Label label, ASTExpression condition);
}
