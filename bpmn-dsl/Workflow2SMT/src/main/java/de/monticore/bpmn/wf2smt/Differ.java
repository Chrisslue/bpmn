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
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Differ {

  private final LTS2SMTEncoding encodedFirst;
  private final LTS2SMTEncoding encodedSecond;
  private final EnumSort<String> labelEnum;
  private final Map<String, Expr<EnumSort<String>>> label2Enum;

  private final Context ctx;


  public Differ(LTS first, LTS second) {
    this.ctx = new Context();
    String[] allLabel = Stream.concat(
            first.allUsedLabels().stream(),
            second.allUsedLabels().stream())
        .distinct().toArray(String[]::new);
    this.labelEnum = ctx.mkEnumSort("LabelSort", allLabel);
    this.label2Enum = IntStream.range(0, allLabel.length)
        .boxed()
        .collect(Collectors.toMap(
            index -> allLabel[index],
            index -> labelEnum.getConsts()[index])
        );
    this.encodedFirst = new LTS2SMTEncoding(first, label2Enum, ctx, "FirstStateSort");
    this.encodedSecond = new LTS2SMTEncoding(second, label2Enum, ctx, "SecondStateSort");
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
      List<Expr<EnumSort<State>>> stateList,
      IntExpr indexOfFinal
  ) {
    Expr<BoolSort> conformsTransitionRelation = Z3Helper.BigAnd(ctx,
        IntStream
            .range(0, labelList.size())
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
      List<Expr<EnumSort<State>>> stateList,
      IntExpr indexOfFinal
  ) {
    return ctx.mkAnd(
        ctx.mkEq(stateList.get(0), ltsEncoding.getStartState()),
        Z3Helper.BigAnd(ctx,
            IntStream
                .range(0, labelList.size())
                .mapToObj(idx -> ctx.mkImplies(ctx.mkEq(indexOfFinal, ctx.mkInt(idx)),
                    ctx.mkEq(labelList.get(idx), label2Enum.get("End")) // TODO
                ))
                .collect(Collectors.toList())
        )
    );
  }

  private static <T> List<T> evaluationOfList(
      Model model,
      List<Expr<EnumSort<T>>> symbolList,
      Map<T, Expr<EnumSort<T>>> bijectiveLookup,
      int size
  ) {
    return symbolList
        .subList(0, size)
        .stream()
        .map(symbolExpr -> model.evaluate(symbolExpr, true))
        .map(symbolEval -> bijectiveLookup
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue().equals(symbolEval))
            .map(Entry::getKey)
            .findFirst()
            .orElseThrow())
        .collect(Collectors.toList());
  }

  private static int evaluationOfInt(Model model, IntExpr indexOfFinal) {
    var evalIndex = model.evaluate(indexOfFinal, true);
    if (!evalIndex.isIntNum()) {
      throw new IllegalArgumentException(indexOfFinal.toString() + "is not evaluated to an int.");
    }
    return ((IntNum) evalIndex).getInt();
  }

  public Optional<Path> findWitness(
      LTS2SMTEncoding first,
      LTS2SMTEncoding second,
      int maxSize
  ) {
    var indexOfFinalEntry = createIndexOfFinal(maxSize);
    IntExpr indexOfFinal = indexOfFinalEntry.getKey();
    BoolExpr indexOfFinalAssertions = indexOfFinalEntry.getValue();

    List<Expr<EnumSort<String>>> label = IntStream
        .range(0, maxSize)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("l" + i), this.labelEnum))
        .collect(Collectors.toList());

    List<Expr<EnumSort<State>>> statesInFirst = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), first.getStateEnum()))
        .collect(Collectors.toList());

    List<Expr<EnumSort<State>>> statesInSecond = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), second.getStateEnum()))
        .collect(Collectors.toList());
    var isTraceInFirst = isValidTraceOver(first, label, statesInFirst, indexOfFinal);

    var traceNotInSecond = ctx.mkForall(statesInSecond.toArray(Expr[]::new),
        ctx.mkNot(isValidTraceOver(second, label, statesInSecond, indexOfFinal)),
        1, null, null, ctx.mkSymbol(Z3Helper.gn("ForAll")), ctx.mkSymbol(Z3Helper.gn("")));
    var solver = ctx.mkSolver();
    var result = solver.check(indexOfFinalAssertions, isTraceInFirst, traceNotInSecond);
    if (result == Status.SATISFIABLE) {
      var indexOfFinalEvaluation = evaluationOfInt(solver.getModel(), indexOfFinal);
      var labelEvaluation = Differ.evaluationOfList(solver.getModel(), label, label2Enum, indexOfFinalEvaluation + 1);
      // indexOfFinalEvaluation + 1 because there are two states for on label in a transition.
      var statesEvaluation = evaluationOfList(
          solver.getModel(), statesInFirst, first.getState2Enum(), indexOfFinalEvaluation + 2);
      var resolvedPath = new LTSTraverser(first.getUnderlyingLTS()).pathOfLabelAndStates(labelEvaluation,
          statesEvaluation);
      if (resolvedPath.isEmpty()) {
        throw new IllegalStateException(
            "Could not resolve found witness " + statesEvaluation
                + " with label " + labelEvaluation
        );
      }
      return resolvedPath;
    } else if (result == Status.UNSATISFIABLE) {
      return Optional.empty();
    } else {
      throw new IllegalStateException("Could not determine result with z3.");
    }
  }

  public void diff(int maxSize) {

    System.out.println("Testing traces(first) subset of traces(second)");
    findWitness(encodedFirst, encodedSecond, maxSize);

    System.out.println("Testing traces(second) subset of traces(first)");
    findWitness(encodedSecond, encodedFirst, maxSize);
  }

  public LTS2SMTEncoding getEncodedFirst() {
    return encodedFirst;
  }

  public LTS2SMTEncoding getEncodedSecond() {
    return encodedSecond;
  }

  public EnumSort<String> getLabelEnum() {
    return labelEnum;
  }

  public Map<String, Expr<EnumSort<String>>> getLabel2Enum() {
    return label2Enum;
  }
}