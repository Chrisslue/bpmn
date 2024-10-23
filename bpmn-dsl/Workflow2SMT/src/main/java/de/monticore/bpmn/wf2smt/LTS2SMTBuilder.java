package de.monticore.bpmn.wf2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Symbol;
import de.monticore.bpmn.wf2smt.LTS2SMTEncoding.TransitionRelation;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lts.LTSBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LTS2SMTBuilder implements LTSBuilder<Symbol, Expr<EnumSort<String>>> {

  private final Context ctx;

  private final Map<String, Expr<EnumSort<String>>> label2Enum;

  private Symbol initialState;

  private final List<SMTTransition> transitions;

  private final Map<String, Symbol> stateEncoding;

  public LTS2SMTBuilder(Context ctx, Map<String, Expr<EnumSort<String>>> label2Enum) {
    this.ctx = ctx;
    this.label2Enum = label2Enum;
    stateEncoding = new HashMap<>();
    transitions = new ArrayList<>();
  }

  @Override
  public void addVariable(String varName, ASTMCQualifiedType varType, ASTExpression value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Expr<EnumSort<String>> addLabel(String label) {
    return label2Enum.get(label);
  }

  @Override
  public Symbol addState(String name) {
    if (stateEncoding.containsKey(name)) {
      return stateEncoding.get(name);
    }
    var sym = ctx.mkSymbol(Z3Helper.uniqueName(name));
    stateEncoding.put(name, sym);
    return sym;
  }

  @Override
  public Symbol addInitialState(String name) {
    if (initialState != null) {
      Log.warn("Initial states get overridden. Previously " + initialState);
    }
    var encoding = addState(name);
    this.initialState = encoding;
    return encoding;
  }

  @Override
  public Symbol addFinalState(String name) {
    return addState(name);
  }

  @Override
  public void addTransition(Symbol source, Symbol target, Expr<EnumSort<String>> label) {
    transitions.add(new SMTTransition(source, label, target));
  }

  @Override
  public void addTransition(
      Symbol source, Symbol target, Expr<EnumSort<String>> symbol, ASTExpression condition) {
    Log.warn("Ignoring " + condition + " ASTExpressions as conditions are not yet implemented");
    addTransition(source, target, symbol);
  }

  public LTS2SMTEncoding build() {
    return build(Z3Helper.uniqueName("StateEnum"));
  }

  public LTS2SMTEncoding build(String stateEnumName) {
    Symbol[] statesAsSymbols = stateEncoding.values().toArray(Symbol[]::new);
    EnumSort<String> stateSort = ctx.mkEnumSort(ctx.mkSymbol(stateEnumName), statesAsSymbols);
    Map<Symbol, Expr<EnumSort<String>>> value2Sort =
        IntStream.range(0, statesAsSymbols.length)
            .boxed()
            .collect(
                Collectors.toMap(
                    index -> statesAsSymbols[index], index -> stateSort.getConsts()[index]));
    TransitionRelation<Expr<EnumSort<String>>, Expr<EnumSort<String>>> transitionRelation =
        (source, label, target) ->
            Z3Helper.BigOr(
                ctx,
                transitions.stream()
                    .map(
                        transition ->
                            ctx.mkAnd(
                                ctx.mkEq(value2Sort.get(transition.source), source),
                                ctx.mkEq(value2Sort.get(transition.target), target),
                                ctx.mkEq(transition.label, label)
                                // TODO Add ASTExpressions here
                                ))
                    .collect(Collectors.toList()));
    var startState = value2Sort.get(initialState);
    return new LTS2SMTEncoding(startState, stateSort, transitionRelation);
  }

  private static class SMTTransition {

    private final Symbol source;
    private final Expr<EnumSort<String>> label;
    private final Symbol target;

    public SMTTransition(Symbol source, Expr<EnumSort<String>> label, Symbol target) {
      this.source = source;
      this.label = label;
      this.target = target;
    }
  }
}
