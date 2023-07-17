package de.monticore.bpmn.wf2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WF2SMTDiffGenerator {

  private WF2SMTDiffGenerator() {

  }

  public static SMTDiff generateDiffer(String firstModelName, String secondModelName) {
    return generateDiffer(
        WF2LTSGenerator.workflow2LTS(firstModelName),
        WF2LTSGenerator.workflow2LTS(secondModelName),
        "End"); // TODO
  }

  public static SMTDiff generateDiffer(LTS firstLTS, LTS secondLTS, String finalSymbol) {
    return generateDiffer(
        (x) -> firstLTS.toModel(x, firstLTS.getNamingStrategy("p")),
        (y) -> secondLTS.toModel(y, secondLTS.getNamingStrategy("q")),
        Stream.concat(
                firstLTS.allUsedLabels().stream(),
                secondLTS.allUsedLabels().stream())
            .distinct()
            .collect(Collectors.toList()),
        finalSymbol
    );
  }

  public static SMTDiff generateDiffer(
      Consumer<LTS2SMTBuilder> supplierForFirst,
      Consumer<LTS2SMTBuilder> supplierForSecond,
      List<String> allLabel,
      String finalSymbol
  ) {
    var ctx = new Context();

    Entry<EnumSort<String>, Map<String, Expr<EnumSort<String>>>> labelSortEntry =
        Z3Helper.toEnum(ctx, "LabelSort", allLabel);
    var labelSort = labelSortEntry.getKey();
    Map<String, Expr<EnumSort<String>>> label2Enum = labelSortEntry.getValue();
    var encodedFirst = encodeUsingBuilder(ctx, label2Enum, supplierForFirst);
    var encodedSecond = encodeUsingBuilder(ctx, label2Enum, supplierForSecond);

    return new SMTDiff(ctx, labelSort, label2Enum.get(finalSymbol), encodedFirst, encodedSecond);
  }

  private static LTS2SMTEncoding encodeUsingBuilder(
      Context ctx,
      Map<String, Expr<EnumSort<String>>> label2Enum,
      Consumer<LTS2SMTBuilder> builderPopulatingConsumer
  ) {
    var builderOfFirst = new LTS2SMTBuilder(ctx, label2Enum);
    builderPopulatingConsumer.accept(builderOfFirst);
    return builderOfFirst.build();
  }

}
