package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.analysis.ConfWfBuilder;
import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.WfElementVisitor;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class WfConformanceChecker {

  private DummyIncarnationStrategy incStrategy;

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    // transform reference and concrete node
    IdWfNode ref = generateNode(reference, "Reference:");
    IdWfNode con = generateNode(reference, "Concrete:");

    incStrategy = new DummyIncarnationStrategy(concrete, reference);

    return true;
  }

  // completely ignore gateway in the algorithm this should be handled  w in the node themselves
  public boolean checkNodeConformance(IdWfNode con, IdWfNode ref) {
    Log.info(
        String.format("Checking conformance of [%s] to [%s]", con.getLabel(), ref.getLabel()),
        this.getClass().getName());

    // check the successors of the concrete node
    Set<WfNode> predecessors = checkAdjacentNodes(con, ref, true);
    if (!predecessors.isEmpty()) {
      Log.warn(
          "Nodes does not conform, the following predecessors of the concrete node break the conformance "
              + predecessors);
    }

    // checking successors
    Set<WfNode> successors = checkAdjacentNodes(con, ref, false);

    if (!successors.isEmpty()) {
      Log.warn(
          "Nodes does not conform, the following successors of the concrete node break the conformance "
              + predecessors);
    }

    Log.info(
        String.format("concrete:[%s] conforms to  reference:[%s]", ref.getLabel(), con.getLabel()),
        this.getClass().getName());
    return true;
  }

  /***
   * check the neighbor (either successors or predecessors ) of a node and check if the node conforms
   * according to the relation that it has to its neighbor.
   * @param conNode the concrete node
   * @return the set of neighbor that break the conformance.
   */
  Set<WfNode> checkAdjacentNodes(
          IdWfNode conNode, IdWfNode refNode, boolean checkingPredecessor) {

    BiPredicate<List<WfNode>, WfNode> refPred = this::nodeIncarnateLastPathNode;

    Set<WfNode> directAdjNodes;
    if (checkingPredecessor) {
      directAdjNodes =
          conNode.allPredecessor(this::lastPathNodeIsIncarnation, 1).parallelStream()
              .filter(
                  node -> refNode.existsPredecessor(path -> refPred.test(path, node), -1).isEmpty())
              .collect(Collectors.toSet());
    } else {
      directAdjNodes =
          conNode.allSuccessors(this::lastPathNodeIsIncarnation, 1).parallelStream()
              .filter(
                  node -> refNode.existsSuccessor(path -> refPred.test(path, node), -1).isEmpty())
              .collect(Collectors.toSet());
    }

    return directAdjNodes;
  }

  public IdWfNode generateNode(ASTWorkflowCompilationUnit ast, String prefix) {

    ConfWfBuilder builder = new ConfWfBuilder(prefix);

    // traverse the Workflow ast a collect elements
    WfElementVisitor collector = new WfElementVisitor(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    return builder.build();
  }

  boolean lastPathNodeIsIncarnation(List<WfNode> path) {
    return incStrategy.isIncarnation(path.get(path.size() - 1));
  }

  boolean nodeIncarnateLastPathNode(List<WfNode> path, WfNode node) {
    return incStrategy.checkIncarnation(node, path.get(path.size() - 1))
        && noIncarnationOfAReferenceInPath(path);
  }

  // Function to check if the sum of two numbers is even
  public static boolean noIncarnationOfAReferenceInPath(List<WfNode> path) {
    for (WfNode node : path) {
      // todo implements
    }

    return true;
  }
}
