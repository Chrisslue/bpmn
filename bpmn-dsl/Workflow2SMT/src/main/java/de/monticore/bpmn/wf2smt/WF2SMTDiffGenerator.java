package de.monticore.bpmn.wf2smt;

import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import de.monticore.bpmn.wf2lts.DefaultNamingStrategy;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEventType;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WF2SMTDiffGenerator {

  private WF2SMTDiffGenerator() {

  }

  public static SMTDiff generateDiffer(String firstModelName, String secondModelName) {
    ASTWorkflowCompilationUnit firstAST = WF2LTSGenerator.loadBPMN(firstModelName);
    ASTWorkflowCompilationUnit secondAST = WF2LTSGenerator.loadBPMN(secondModelName);

    var namingStrategy = new DefaultNamingStrategy();
    Set<String> endEvents = new HashSet<>();
    var allPossibleEndEventVisitor = new WorkflowVisitor2() {
      @Override
      public void visit(ASTInlineEvent node) {
        if (node.getType() == ASTEventType.END) {
          endEvents.add(namingStrategy.apply(node));
        }
      }

      @Override
      public void visit(ASTNamedEvent node) {
        if (node.getType() == ASTEventType.END) {
          endEvents.add(namingStrategy.apply(node));
        }
      }
    };
    var traverser = WorkflowMill.traverser();
    traverser.add4Workflow(allPossibleEndEventVisitor);
    firstAST.accept(traverser);
    secondAST.accept(traverser);

    if (endEvents.isEmpty()) {
      Log.error("Could not find end events. Neither in " + firstModelName + " nor in " + secondModelName);
    }

    var firstLTS = WF2LTSGenerator.workflow2LTS(firstAST);
    var secondLTS = WF2LTSGenerator.workflow2LTS(secondAST);

    return generateDiffer(
        firstLTS,
        secondLTS,
        new ArrayList<>(endEvents));
  }

  public static SMTDiff generateDiffer(LTS firstLTS, LTS secondLTS, List<String> finalSymbols) {
    return generateDiffer(
        (x) -> firstLTS.toModel(x, firstLTS.getNamingStrategy("p")),
        (y) -> secondLTS.toModel(y, secondLTS.getNamingStrategy("q")),
        Stream.concat(
                firstLTS.allUsedLabels().stream(),
                secondLTS.allUsedLabels().stream())
            .distinct()
            .collect(Collectors.toList()),
        finalSymbols
    );
  }

  public static SMTDiff generateDiffer(
      Consumer<LTS2SMTBuilder> supplierForFirst,
      Consumer<LTS2SMTBuilder> supplierForSecond,
      List<String> allLabel,
      List<String> finalSymbols
  ) {
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
      Consumer<LTS2SMTBuilder> builderPopulatingConsumer
  ) {
    var builderOfFirst = new LTS2SMTBuilder(ctx, label2Enum);
    builderPopulatingConsumer.accept(builderOfFirst);
    return builderOfFirst.build();
  }

}
