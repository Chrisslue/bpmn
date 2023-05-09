package de.monticore.ltl;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

public interface LTLBuilder<State, Label, Exp extends ASTExpression> {

    State addState(String name);

    State addInitialState(String name);


    void addTransition(State source, State target, Label label, Exp condition);
}
