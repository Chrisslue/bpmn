package de.monticore.bpmn.wf2smt;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;

public class LTS2SMTEncoding {

  private final Expr<EnumSort<String>> startState;
  private final EnumSort<String> stateEnum;

  private final TransitionRelation<Expr<EnumSort<String>>, Expr<EnumSort<String>>> transitionRelation;

  public LTS2SMTEncoding(
      Expr<EnumSort<String>> startState,
      EnumSort<String> stateEnum,
      TransitionRelation<Expr<EnumSort<String>>, Expr<EnumSort<String>>> transitionRelation) {
    this.startState = startState;
    this.stateEnum = stateEnum;
    this.transitionRelation = transitionRelation;
  }

  public Expr<EnumSort<String>> getStartState() {
    return startState;
  }

  public EnumSort<String> getStateEnum() {
    return stateEnum;
  }

  public TransitionRelation<Expr<EnumSort<String>>, Expr<EnumSort<String>>> getTransitionRelation() {
    return transitionRelation;
  }

  public interface TransitionRelation<S, L> {

    Expr<BoolSort> isTransition(S source, L label, S target);
  }

}
