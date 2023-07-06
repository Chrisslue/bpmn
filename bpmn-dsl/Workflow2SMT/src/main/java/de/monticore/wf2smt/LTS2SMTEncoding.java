package de.monticore.wf2smt;

import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LTS2SMTEncoding {

  private final Expr<EnumSort<State>> startState;
  private final EnumSort<State> stateEnum;
  private final Map<State, Expr<EnumSort<State>>> state2Enum;

  private final Map<String, Expr<EnumSort<String>>> label2Enum;

  private final TransitionRelation<Expr<EnumSort<State>>, Expr<EnumSort<String>>> transitionRelation;
  private final LTS underlyingLTS;

  public LTS2SMTEncoding(LTS lts, Map<String, Expr<EnumSort<String>>> label2Enum, Context ctx) {
    this(lts, label2Enum, ctx, lts.toString());
  }

  public LTS2SMTEncoding(LTS lts, Map<String, Expr<EnumSort<String>>> label2Enum, Context ctx, String stateEnumName) {
    this.underlyingLTS = lts;
    var stateEnumWithMap = Z3Helper.toEnum(ctx, stateEnumName, lts.getStates());
    this.stateEnum = stateEnumWithMap.getKey();
    this.state2Enum = stateEnumWithMap.getValue();
    this.startState = state2Enum.get(lts.getStart());
    this.label2Enum = label2Enum;
    var allTransitions = lts.allUsedLabels()
        .stream()
        .flatMap(label -> lts.getTransitionsForLabel(label).stream())
        .collect(Collectors.toList());
    this.transitionRelation = buildTransitionRelation(ctx, state2Enum, label2Enum, allTransitions);
  }

  public Expr<EnumSort<State>> getStartState() {
    return startState;
  }

  public EnumSort<State> getStateEnum() {
    return stateEnum;
  }

  public Map<State, Expr<EnumSort<State>>> getState2Enum() {
    return state2Enum;
  }

  public Map<String, Expr<EnumSort<String>>> getLabel2Enum() {
    return label2Enum;
  }

  public TransitionRelation<Expr<EnumSort<State>>, Expr<EnumSort<String>>> getTransitionRelation() {
    return transitionRelation;
  }

  public LTS getUnderlyingLTS() {
    return underlyingLTS;
  }

  public static <E extends EnumSort<?>, S extends EnumSort<?>> TransitionRelation<Expr<S>, Expr<E>> buildTransitionRelation(
      Context context,
      Map<LTS.State, Expr<S>> state2Id,
      Map<String, Expr<E>> label2Enum,
      List<LTS.Transition> transitions
  ) {

    return (source, label, target) ->
        Z3Helper.BigOr(context, transitions.stream().map(transition ->
            context.mkAnd(
                context.mkEq(state2Id.get(transition.getSource()), source),
                context.mkEq(state2Id.get(transition.getTarget()), target),
                context.mkEq(label2Enum.get(transition.getLabel()), label)
                // TODO Add ASTExpressions here
            )
        ).collect(Collectors.toList()));
  }

  public interface TransitionRelation<S, L> {

    Expr<BoolSort> isTransition(S source, L label, S target);
  }

}
