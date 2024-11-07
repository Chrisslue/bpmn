package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNodeBuilder;
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
    CTLNode root = buildNode(Set.of(statNode), statNode.getSuccessors().iterator().next());

    while (!leaves.isEmpty()) {
      System.out.println();

      for (int i = 0; i < leaves.size(); i++) {
        performRound(leaves.get(i));
      }
      Log.info(String.format("We now have : %s leaves", leaves.size()), this.getClass().getName());
      Log.info(String.format(leaves.toString()), this.getClass().getName());
      Log.println("");
    }

    // create root node of CTL

    return graph;
  }

  public void performRound(CTLNode vertex) {
    this.leaves.remove(vertex);
    IDWfNode activeNode = vertex.getActiveNodes();

    switch (activeNode.getNodeType()) {
      case EVENT:
      case TASK:
        for (IDWfNode suc : activeNode.getSuccessors()) {
          doTransition(vertex, Set.of(vertex.getActiveNodes()), suc);
        }
        break;
      case XOR_SPLIT:
        for (IDWfNode suc : activeNode.getSuccessors()) {
          for (IDWfNode sucSuc : suc.getSuccessors()) {
            doTransition(vertex, Set.of(suc), sucSuc);
          }
        }

        break;

      case XOR_MERGE:
        Set<IDWfNode> act2 =
            activeNode.getSuccessors().stream()
                .map(IDWfNode::getSuccessors)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        for (IDWfNode suc : act2) {
          doTransition(vertex, activeNode.getSuccessors(), suc);
        }
        break;

      case AND_SPLIT:
        for (IDWfNode suc : activeNode.getSuccessors()) {
          doTransition(vertex, activeNode.getSuccessors(), suc);
        }
        break;
    }
  }

  public void doTransition(CTLNode previous, Set<IDWfNode> newLabels, IDWfNode nextActivated) {
    Set<IDWfNode> labels = new HashSet<>(previous.getLabels());
    labels.addAll(newLabels);

    CTLNode sucNode = buildNode(labels, nextActivated);

    graph.addEdge(previous, sucNode);
  }

  CTLNode buildNode(Set<IDWfNode> labels, IDWfNode activeNodes) {
    CTLNode res = new CTLNode(labels, activeNodes);
    graph.addNode(res);
    leaves.add(res);

    Log.info(String.format("Adding %s to the Graph", res), this.getClass().getName());

    return res;
  }
}
