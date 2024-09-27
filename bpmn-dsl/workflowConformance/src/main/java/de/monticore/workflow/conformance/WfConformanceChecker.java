package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.analysis.ConfWfBuilder;
import de.monticore.workflow.conformance.datastructure.analysis.ConfWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.WfElementVisitor;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WfConformanceChecker {

  private DummyIncarnationStrategy incStrategy;

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    // transform reference and concrete node
    ConfWfNode ref = generateNode(reference, "Reference:");
    ConfWfNode con = generateNode(reference, "Concrete:");

    incStrategy = new DummyIncarnationStrategy(concrete, reference);
    con.setIncStrategy(incStrategy);
    ref.setIncStrategy(incStrategy);

    return checkConformanceAlgorithm(con, ref);
  }

  // completely ignore gateway in the algorithm this should be handled  w in the node themselves
  public boolean checkConformanceAlgorithm(ConfWfNode con, ConfWfNode ref) {

    Log.info(
        String.format("Checking conformance of [%s] to [%s]", con.getLabel(), ref.getLabel()),
        this.getClass().getName());

    if (!incStrategy.checkIncarnation(ref, con)) {
      return false;
    }
    // todo addd helper method for predecessor and successors

    // all direct predecessors of the concrete node
    Set<WfNode> directPredecessors =
        con.allPredecessor((path, node) -> incStrategy.isIncarnation(node), 1);

    for (WfNode conPred : directPredecessors) {
      // check that the reference node has a predecessor refPred (of any depth) that incarnate
      // but without incarnation of a reference node in between
      Optional<WfNode> refPred =
          ref.existsPredecessor(
              (path, node) ->
                  noIncarnationOfAReferenceInPath(path)
                      && incStrategy.checkIncarnation(conPred, node),
              -1);

      // return false if the check result is negative
      if (refPred.isEmpty()) {
        return false;
      }
    }

    // all direct successors of the concrete node
    Set<ConfWfNode> directSuccessors = con.allSuccessors((path, node) -> true, 1);

    for (ConfWfNode conSuc : directSuccessors) {
      // check that the reference node has a successors conSuc (of any depth) that incarnate refSuc,
      // but without incarnation of a concrete node in between
      Optional<ConfWfNode> refSuc =
          ref.existsSuccessor(
              (path, node) ->
                  noIncarnationOfAReferenceInPath(path)
                      && incStrategy.checkIncarnation(conSuc, node),
              -1);

      // return false if the check result is negative
      if (refSuc.isEmpty()) {
        return false;
      }

      // recursively check conformance

      if (!checkConformanceAlgorithm(conSuc, refSuc.get())) { // todo  no recursion
        return false;
      }
    }

    // the algorithm will stop either when something is not conform or when the current node have no
    // successors
    Log.info(
        String.format("concrete:[%s] conforms to  reference:[%s]", ref.getLabel(), con.getLabel()),
        this.getClass().getName());
    return true;
  }

  // Function to check if the sum of two numbers is even
  public static boolean noIncarnationOfAReferenceInPath(List<WfNode> path) {
    for (WfNode node : path) {
      // todo implements
    }

    return true;
  }

  public ConfWfNode generateNode(ASTWorkflowCompilationUnit ast, String prefix) {

    ConfWfBuilder builder = new ConfWfBuilder(prefix);

    // traverse the Workflow ast a collect elements
    WfElementVisitor collector = new WfElementVisitor(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    return builder.build();
  }
}
