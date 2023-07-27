package de.monticore.bpmn.wf2smt;

import static de.monticore.bpmn.wf2smt.Z3Helper.allIndicesMatch;
import static de.monticore.bpmn.wf2smt.Z3Helper.matchesAny;
import static java.util.Map.entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Status;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMTDiff {

  private final EnumSort<String> labelSort;
  private final List<Expr<EnumSort<String>>> finalSymbols;
  private final LTS2SMTEncoding encodedFirst;
  private final LTS2SMTEncoding encodedSecond;

  private final Context ctx;


  public SMTDiff(Context ctx,
      EnumSort<String> labelSort,
      List<Expr<EnumSort<String>>> finalSymbols,
      LTS2SMTEncoding first,
      LTS2SMTEncoding second
  ) {
    this.ctx = ctx;
    this.labelSort = labelSort;
    this.finalSymbols = finalSymbols;
    this.encodedFirst = first;
    this.encodedSecond = second;
  }


  private Entry<IntExpr, BoolExpr> createIndexOfFinal(int maxSize) {
    var indexOfFinal = ctx.mkIntConst(Z3Helper.gn("IndexOfFinal"));
    // The "real" trace length has to be >= 0 and smaller than the maxSize.
    var indexOfFinalAssertions = ctx.mkAnd(
        ctx.mkGe(indexOfFinal, ctx.mkInt(0)),
        ctx.mkLt(indexOfFinal, ctx.mkInt(maxSize))
    );
    return entry(indexOfFinal, indexOfFinalAssertions);
  }

  public Expr<BoolSort> isValidTraceOver(
      LTS2SMTEncoding encodedLTS,
      List<Expr<EnumSort<String>>> labelList,
      List<Expr<EnumSort<String>>> stateList,
      IntExpr indexOfFinal
  ) {
    // For all 0 <= i <= indexOfFinal the transition relation has to hold.
    Expr<BoolSort> conformsTransitionRelation =
        allIndicesMatch(ctx, labelList,
            idx -> ctx.mkImplies(ctx.mkLe(ctx.mkInt(idx), indexOfFinal),
                encodedLTS.getTransitionRelation()
                    .isTransition(stateList.get(idx), labelList.get(idx), stateList.get(idx + 1)))
        );
    return ctx.mkAnd(conformsTransitionRelation,
        traceHasCorrectStartAndEnd(encodedLTS, labelList, stateList, indexOfFinal
        ));
  }

  public Expr<BoolSort> traceHasCorrectStartAndEnd(
      LTS2SMTEncoding ltsEncoding,
      List<Expr<EnumSort<String>>> labelList,
      List<Expr<EnumSort<String>>> stateList,
      IntExpr indexOfFinal
  ) {
    return ctx.mkAnd(
        ctx.mkEq(stateList.get(0), ltsEncoding.getStartState()), // Trace starts with start state.
        // If idx == indexOfFinal => the label at idx has to be a final symbol.
        // That normally means the trace has to end with a label of an end event.
        allIndicesMatch(ctx, labelList,
            idx -> ctx.mkImplies(
                ctx.mkEq(indexOfFinal, ctx.mkInt(idx)),
                matchesAny(ctx, labelList.get(idx), finalSymbols)
            )));
  }

  public Optional<List<String>> firstSubsetOfSecond(int maxSize) {
    return findWitness(this.encodedFirst, this.encodedSecond, maxSize);
  }

  public Optional<List<String>> secondSubsetOfFirst(int maxSize) {
    return findWitness(this.encodedSecond, this.encodedFirst, maxSize);
  }

  private Optional<List<String>> findWitness(
      LTS2SMTEncoding first,
      LTS2SMTEncoding second,
      int maxSize
  ) {
    var indexOfFinalEntry = createIndexOfFinal(maxSize);
    IntExpr indexOfFinal = indexOfFinalEntry.getKey();
    BoolExpr indexOfFinalAssertions = indexOfFinalEntry.getValue();

    // The trace is defined by the [state0, label0, state1, ..., label_n, state_n+1 ].
    // We require the labelList and statesInFirst to be a valid trace in the first diagram.
    // If no combination of states (length =n+1) can be created for second, which would allow the same
    // trace of label than this is a witness for a trace that is possible in first but not in second.
    List<Expr<EnumSort<String>>> labelList = IntStream
        .range(0, maxSize)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("l" + i), this.labelSort))
        .collect(Collectors.toList());

    List<Expr<EnumSort<String>>> statesInFirst = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), first.getStateEnum()))
        .collect(Collectors.toList());

    List<Expr<EnumSort<String>>> statesInSecond = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), second.getStateEnum()))
        .collect(Collectors.toList());
    var isTraceInFirst = isValidTraceOver(first, labelList, statesInFirst, indexOfFinal);

    // There is no combination of states such that those would allow the same path of label in second.
    var traceNotInSecond = ctx.mkForall(statesInSecond.toArray(Expr[]::new),
        ctx.mkNot(isValidTraceOver(second, labelList, statesInSecond, indexOfFinal)),
        1, null, null, ctx.mkSymbol(Z3Helper.gn("ForAll")), ctx.mkSymbol(Z3Helper.gn("")));

    var solver = ctx.mkSolver();
    var result = solver.check(indexOfFinalAssertions, isTraceInFirst, traceNotInSecond);
    if (result == Status.SATISFIABLE) {
      var indexOfFinalEvaluation = Z3Helper.evaluationOfInt(solver.getModel(), indexOfFinal);
      var labelEvaluation = Z3Helper.evaluationOfList(solver.getModel(), labelList, indexOfFinalEvaluation + 1);
      return Optional.of(labelEvaluation);

    } else if (result == Status.UNSATISFIABLE) {
      return Optional.empty();
    } else {
      throw new IllegalStateException("Could not determine result with z3.");
    }
  }

  public LTS2SMTEncoding getEncodedFirst() {
    return encodedFirst;
  }

  public LTS2SMTEncoding getEncodedSecond() {
    return encodedSecond;
  }

}