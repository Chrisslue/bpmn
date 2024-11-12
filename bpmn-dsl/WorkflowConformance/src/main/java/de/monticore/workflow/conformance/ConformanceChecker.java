package de.monticore.workflow.conformance;

import static de.monticore.workflow.conformance.utils.BPMNUtils.generateIDWfNode;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.conformance.ConfWfTraverser;
import de.monticore.workflow.conformance.conformance.ConfWfVisitor;
import de.monticore.workflow.conformance.conformance.PredicateGenerator;
import de.monticore.workflow.conformance.datastructure.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.incarnation.NameIncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ConformanceChecker {

  private final String logName = this.getClass().getSimpleName();

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    String conName = concrete.getProcess().getName();
    String refName = reference.getProcess().getName();
    Log.println("");
    Log.info(
        String.format(
            "Start Checking Conformance of Concrete:%s to Reference:%s.", conName, refName),
        logName);

    // transform reference and concrete node
    // todo fix  identifier for different strategies
    IDWfNodeBuilder ref = generateIDWfNode(reference, i -> ":" + i);
    IDWfNodeBuilder con = generateIDWfNode(concrete, i -> ":" + i);

    NameIncarnationStrategy inc = new NameIncarnationStrategy(ref, con);

    Predicate<List<WfNode>> refPredicate = PredicateGenerator.postPredicate(ref.getStartEvent());

    ConfWfVisitor visitor = new ConfWfVisitor(refPredicate, inc);

    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(visitor, new ArrayList<>(), con.getStartEvent());

    Log.info(
        String.format(
            "End of the conformance check of Concrete:%s to Reference:%s.", conName, refName),
        logName);

    Log.info(visitor.printStatistics(), "");

    Log.println("");
    return visitor.getResult();
  }
}
