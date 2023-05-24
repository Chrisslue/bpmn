package de.monticore.ltl;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import java.util.Collection;

public interface LTSBuilder<State, Label> {

  void addVariable(String varName, ASTMCQualifiedType varType, ASTExpression value);

  Label addLabel(String label);

  State addState(String name);

  State addInitialState(String name);

  void addTransition(State source, State target, Label label, Collection<ASTExpression> conditions);
}
