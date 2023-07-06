package de.monticore.wf2smt;

import static java.util.Map.entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

  private void printEvaluation(
      Model model,
      List<Expr<EnumSort<String>>> labelList,
      List<Expr<EnumSort<State>>> stateList,
      IntExpr indexOfFinal
  ) {
    var finalIndex = Integer.valueOf(model.evaluate(indexOfFinal, false).toString());
    System.out.println(finalIndex);
    var evalLabels = labelList
        .subList(0, finalIndex + 1)
        .stream()
        .map(l -> model.evaluate(l, false))
        .collect(Collectors.toList());
    System.out.println(evalLabels);
    var evalStates = stateList
        .subList(0, finalIndex + 2)
        .stream()
        .map(s -> model.evaluate(s, false))
        .collect(Collectors.toList());
    System.out.println(evalStates);
  }

  public void findWitness(
      int maxSize
  ) {

    var indexOfFinalEntry = createIndexOfFinal(maxSize);
    var indexOfFinal = indexOfFinalEntry.getKey();
    var indexOfFinalAssertions = indexOfFinalEntry.getValue();

    List<Expr<EnumSort<String>>> label = IntStream
        .range(0, maxSize)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("l" + i), this.labelEnum))
        .collect(Collectors.toList());

    List<Expr<EnumSort<State>>> statesInFirst = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), encodedFirst.getStateEnum()))
        .collect(Collectors.toList());

    var statesInSecond = IntStream
        .range(0, maxSize + 1)
        .mapToObj(i -> ctx.mkConst(Z3Helper.gn("s" + i), encodedSecond.getStateEnum()))
        .collect(Collectors.toList());

    var isTraceInFirst = isValidTraceOver(encodedFirst, label, statesInFirst, indexOfFinal);

    var solver = ctx.mkSolver();
    solver.add(indexOfFinalAssertions, isTraceInFirst);
    var result = solver.check();
    if (result == Status.SATISFIABLE) {
      printEvaluation(solver.getModel(), label, statesInFirst, indexOfFinal);
    }
  }

}