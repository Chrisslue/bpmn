package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.WfNodeGenerator;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WfConformanceChecker {

  private DummyIncarnationStrategy incStrategy = new DummyIncarnationStrategy();

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    // transform reference and concrete node
    WfNodeGenerator generator = new WfNodeGenerator();
    WfNode ref = generator.generateNode(reference);
    WfNode con = generator.generateNode(concrete);

    return checkConformanceAlgorithm(con, ref);
  }

  // completely ignore gateway in the algorithm this should be handled  w in the node themselves
  public boolean checkConformanceAlgorithm(WfNode con, WfNode ref) {
    Log.info(
        String.format(
            "Checking conformance of concrete:[%s] to  reference:[%s]",
            ref.getLabel(), con.getLabel()),
        this.getClass().getName());

    if (!incStrategy.isIncarnation(ref, con)) {
      return false;
    }

    // all direct predecessors of the concrete node
    Set<WfNode> directPredecessors = con.allPredecessor((path, node) -> true, 1);

    for (WfNode refPred : directPredecessors) {
      // check that the reference node has a predecessor refPred (of any depth) that incarnate
      // but without incarnation of a reference node in between
      Optional<WfNode> conPred =
          ref.existsPredecessor(
              (path, node) ->
                  noIncarnationOfAReferenceInPath(path) && incStrategy.isIncarnation(node, refPred),
              -1);

      // return false if the check result is negative
      if (conPred.isEmpty()) {
        return false;
      }
    }

    // all direct successors of the concrete node
    Set<WfNode> directSuccessors = con.allSuccessors((path, node) -> true, 1);

    for (WfNode conSuc : directSuccessors) {
      // check that the reference node has a successors conSuc (of any depth) that incarnate refSuc,
      // but without incarnation of a concrete node in between
      Optional<WfNode> refSuc =
          ref.existsSuccessor(
              (path, node) ->
                  noIncarnationOfAReferenceInPath(path) && incStrategy.isIncarnation(conSuc, node),
              -1);

      // return false if the check result is negative
      if (refSuc.isEmpty()) {
        return false;
      }

      // recursively check conformance
      if (!checkConformanceAlgorithm(conSuc, refSuc.get())) {
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
}
