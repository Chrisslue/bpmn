package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CTLGenerator {
  List<CTLNode> leaves = new ArrayList<>();
  CTLGraph graph = new CTLGraph();

  public CTLGraph buildCTL(ASTWorkflowCompilationUnit ast) {

    // transform ast into intermediate data structure

    IDWfNodeBuilder start = BPMNUtils.generateIDWfNode(ast, "");

    // case we are at the root node

    IDWfNode statNode = start.build();
    CTLNode root = addNode(Set.of(statNode), statNode.getSuccessors());

    leaves.add(root);

    while (!leaves.isEmpty()) {
      Set<CTLNode> leavesCopy = new HashSet<>(leaves);
      leaves.clear();

      System.out.println();
        for (CTLNode leaf : leavesCopy) { //todo define a new set

          if (leaf.getActiveNodes().isEmpty()){
            leaves.remove(leaf);
          }else {
            leaves.addAll( performRound(leaf) );
          }

        }
      Log.info(String.format("We now have : %s leaves", leaves.size()), this.getClass().getName());
      Log.info(String.format(leaves.toString()), this.getClass().getName());
      Log.println("");
    }



    return graph;
  }

  public Set<CTLNode> performRound(CTLNode vertex) {


    boolean andMerged  = false;

    Set<CTLNode> newBranches = new HashSet<>(Set.of(vertex));

    Set<IDWfNode> andMergeList = resolveAndMergeActiveNode(vertex);



    // first check if they are node that can be merged

    //fixme for now  i just suppose that  i have a single parallel execution at time
    if (!andMergeList.isEmpty() && vertex.getActiveNodes().size() == 1) {

      IDWfNode andMerge = andMergeList.iterator().next();
      Set<CTLNode> newBranches2 = new HashSet<>();
      for (CTLNode prev : newBranches) {
        CTLNode branch = doTransition(prev, andMerge.getSuccessors(), andMerge.getSuccessorsOfDepth(2));
        newBranches2.add(branch);
      }
      newBranches = newBranches2;
      andMerged = true ;
    }

    vertex.getActiveNodes().removeAll(andMergeList);


    for (IDWfNode actNode : vertex.getActiveNodes()) {
      Set<CTLNode> newBranches2 = new HashSet<>();
      switch (actNode.getNodeType()) {
        case EVENT:
        case TASK:
          for (CTLNode prev : newBranches) {
            CTLNode branch =
                doTransition(prev, vertex.getActiveNodes(), actNode.getSuccessors());
            newBranches2.add(branch);
          }
          break;

        case XOR_SPLIT:
          for (CTLNode prev : newBranches) {
            for (IDWfNode suc : actNode.getSuccessors()) {
              CTLNode branch = doTransition(prev, Set.of(suc), suc.getSuccessors());
              newBranches2.add(branch);
            }
          }

          break;

        case XOR_MERGE:
          for (CTLNode prev : newBranches) {
            CTLNode branch = doTransition(prev, actNode.getSuccessors(), actNode.getSuccessorsOfDepth(2));
            newBranches2.add(branch);
          }

          break;

        case AND_SPLIT:
          for (CTLNode prev : newBranches) {
            CTLNode branch = doTransition(prev, actNode.getSuccessors(), actNode.getSuccessorsOfDepth(2));
            newBranches2.add(branch);
          }
          break;

          case AND_MERGE:
            break;
      }
      newBranches = newBranches2;
    }

    if (!andMerged){
      newBranches.forEach(branch->branch.addActiveNodes(andMergeList));
    }
    return  newBranches;
  }

  private Set<IDWfNode> resolveAndMergeActiveNode(CTLNode vertex) {
    return vertex.getActiveNodes().stream()
        .filter(n -> n.getNodeType().equals(NodeType.AND_MERGE))
        .collect(Collectors.toSet());
  }

  public CTLNode doTransition(
      CTLNode previous, Set<IDWfNode> newLabels, Set<IDWfNode> nextActivated) {
    Set<IDWfNode> labels = new HashSet<>(previous.getLabels());
    labels.addAll(newLabels);

    CTLNode sucNode = addNode(labels, nextActivated);

    graph.addEdge(previous, sucNode);

    return sucNode;
  }

  CTLNode addNode(Set<IDWfNode> labels, Set<IDWfNode> activeNodes) {

    CTLNode res = graph.addNode(labels, activeNodes);

    Log.info(String.format("Adding %s to the Graph", res), this.getClass().getName());

    return res;
  }
}
