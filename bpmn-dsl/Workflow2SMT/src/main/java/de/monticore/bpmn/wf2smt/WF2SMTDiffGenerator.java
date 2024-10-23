package de.monticore.bpmn.wf2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import de.monticore.bpmn.wf2lts.UniqueStartAndEndEventNaming;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.transformer.DefaultGatewayInterleaving;
import de.monticore.bpmn.wf2lts.transformer.DefaultGatewayTransformer;
import de.monticore.bpmn.wf2lts.transformer.DefaultGraph2LTSTransformer;
import de.monticore.bpmn.wf2lts.transformer.DefaultSubprocessTransformer;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WF2SMTDiffGenerator {

  private WF2SMTDiffGenerator() {}

  public static SMTDiff generateDiffer(
      String firstModelName,
      String secondModelName,
      String startName,
      String endName,
      String terminatingName) {
    ASTWorkflowCompilationUnit firstAST = WF2LTSGenerator.loadBPMN(firstModelName);
    ASTWorkflowCompilationUnit secondAST = WF2LTSGenerator.loadBPMN(secondModelName);

    var namingStrategy = new UniqueStartAndEndEventNaming(startName, endName, terminatingName);

    var graphTransformer =
        new DefaultGraph2LTSTransformer(
            namingStrategy,
            new DefaultGatewayTransformer(new DefaultGatewayInterleaving(), namingStrategy),
            new DefaultSubprocessTransformer());

    var firstLTS = WF2LTSGenerator.workflow2LTS(firstAST, graphTransformer);
    var secondLTS = WF2LTSGenerator.workflow2LTS(secondAST, graphTransformer);

    return generateDiffer(
        firstLTS,
        secondLTS,
        List.of(namingStrategy.getEndName(), namingStrategy.getTerminatingName()));
  }

  public static SMTDiff generateDiffer(LTS firstLTS, LTS secondLTS, List<String> finalSymbols) {
    return generateDiffer(
        (x) -> firstLTS.toModel(x, firstLTS.getNamingStrategy("p")),
        (y) -> secondLTS.toModel(y, secondLTS.getNamingStrategy("q")),
        allUsedLabel(firstLTS, secondLTS, finalSymbols),
        finalSymbols);
  }

  private static List<String> allUsedLabel(LTS first, LTS second, List<String> finalSymbols) {
    var allUsedLabel = new HashSet<String>();
    allUsedLabel.addAll(first.allUsedLabels());
    allUsedLabel.addAll(second.allUsedLabels());
    allUsedLabel.addAll(finalSymbols);
    return new ArrayList<>(allUsedLabel);
  }

  public static SMTDiff generateDiffer(
      Consumer<LTS2SMTBuilder> supplierForFirst,
      Consumer<LTS2SMTBuilder> supplierForSecond,
      List<String> allLabel,
      List<String> finalSymbols) {
    var ctx = new Context();

    Entry<EnumSort<String>, Map<String, Expr<EnumSort<String>>>> labelSortEntry =
        Z3Helper.toEnum(ctx, "LabelSort", allLabel);
    var labelSort = labelSortEntry.getKey();
    Map<String, Expr<EnumSort<String>>> label2Enum = labelSortEntry.getValue();
    var encodedFirst = encodeUsingBuilder(ctx, label2Enum, supplierForFirst);
    var encodedSecond = encodeUsingBuilder(ctx, label2Enum, supplierForSecond);
    var finalSymbol2Enum = finalSymbols.stream().map(label2Enum::get).collect(Collectors.toList());
    return new SMTDiff(ctx, labelSort, finalSymbol2Enum, encodedFirst, encodedSecond);
  }

  private static LTS2SMTEncoding encodeUsingBuilder(
      Context ctx,
      Map<String, Expr<EnumSort<String>>> label2Enum,
      Consumer<LTS2SMTBuilder> builderPopulatingConsumer) {
    var builderOfFirst = new LTS2SMTBuilder(ctx, label2Enum);
    builderPopulatingConsumer.accept(builderOfFirst);
    return builderOfFirst.build();
  }
}
