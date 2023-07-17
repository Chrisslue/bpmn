package de.monticore.bpmn.wf2smt;

import static java.util.Map.entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SMTDiff {

  private final EnumSort<String> labelSort;
  private final Expr<EnumSort<String>> finalSymbol;
  private final LTS2SMTEncoding encodedFirst;
  private final LTS2SMTEncoding encodedSecond;

  private final Context ctx;


  public SMTDiff(Context ctx,
      EnumSort<String> labelSort,
      Expr<EnumSort<String>> finalSymbol,
      LTS2SMTEncoding first,
      LTS2SMTEncoding second
  ) {
    this.ctx = ctx;
    this.labelSort = labelSort;
    this.finalSymbol = finalSymbol;
    this.encodedFirst = first;
    this.encodedSecond = second;
  }


  private Entry<IntExpr, BoolExpr> createIndexOfFinal(int maxSize) {
    var indexOfFinal = ctx.mkIntConst(Z3Helper.gn("IndexOfFinal"));
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
    Expr<BoolSort> conformsTransitionRelation = Z3Helper.BigAnd(ctx,
        IntStream.range(0, labelList.size())
            .mapToObj(
                idx -> ctx.mkImplies(ctx.mkLe(ctx.mkInt(idx), indexOfFinal),
                    encodedLTS.getTransitionRelation()
                        .isTransition(stateList.get(idx), labelList.get(idx), stateList.get(idx + 1))))
            .collect(Collectors.toList()));
    return ctx.mkAnd(conformsTransitionRelation, traceHasCorrectStartAndEnd(
        encodedLTS,
        labelList,
        stateList,
        indexOfFinal
    ));
  }

  public Expr<BoolSort> traceHasCorrectStartAndEnd(
      LTS2SMTEncoding ltsEncoding,
      List<Expr<EnumSort<String>>> labelList,
      List<Expr<EnumSort<String>>> stateList,
      IntExpr indexOfFinal
  ) {
    return ctx.mkAnd(
        ctx.mkEq(stateList.get(0), ltsEncoding.getStartState()),
        Z3Helper.BigAnd(ctx,
            IntStream
                .range(0, labelList.size())
                .mapToObj(idx -> ctx.mkImplies(ctx.mkEq(indexOfFinal, ctx.mkInt(idx)),
                    ctx.mkEq(labelList.get(idx), finalSymbol) // TODO
                ))
                .collect(Collectors.toList())
        )
    );
  }

  private static <T> List<String> evaluationOfList(
      Model model,
      List<Expr<EnumSort<T>>> symbolList,
      int size
  ) {
    return symbolList
        .subList(0, size)
        .stream()
        .map(symbolExpr -> model.evaluate(symbolExpr, true).toString())
        .collect(Collectors.toList());
  }

  private static int evaluationOfInt(Model model, IntExpr indexOfFinal) {
    var evalIndex = model.evaluate(indexOfFinal, true);
    if (!evalIndex.isIntNum()) {
      throw new IllegalArgumentException(indexOfFinal.toString() + "is not evaluated to an int.");
    }
    return ((IntNum) evalIndex).getInt();
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

    var traceNotInSecond = ctx.mkForall(statesInSecond.toArray(Expr[]::new),
        ctx.mkNot(isValidTraceOver(second, labelList, statesInSecond, indexOfFinal)),
        1, null, null, ctx.mkSymbol(Z3Helper.gn("ForAll")), ctx.mkSymbol(Z3Helper.gn("")));
    var solver = ctx.mkSolver();
    var result = solver.check(indexOfFinalAssertions, isTraceInFirst, traceNotInSecond);
    if (result == Status.SATISFIABLE) {
      var indexOfFinalEvaluation = evaluationOfInt(solver.getModel(), indexOfFinal);
      var labelEvaluation = SMTDiff.evaluationOfList(solver.getModel(), labelList, indexOfFinalEvaluation + 1);
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