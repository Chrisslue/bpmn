package de.monticore.bpmn.conformance.conformance.ctlConformance;

import static de.monticore.bpmn.conformance.datastructures.utils.NodeType.OR_MERGE;

import com.google.common.collect.Sets;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class BFSConfWfTraverser {

  private final Map<WfNode, Set<BranchID>> nextNodes = new HashMap<>();
  private final Set<WfNode> visitedNodes = new HashSet<>();
  private final BranchVisitor visitor;
  private int stepCounter = 0;

  public BFSConfWfTraverser(BranchVisitor wfVisitor, WfNode startNode) {
    this.visitor = wfVisitor;
    nextNodes.put(startNode, new HashSet<>(List.of(new BranchID(new ArrayList<>()))));
  }

  public boolean stepForward() {
    Log.println("");
    Log.trace(String.format("--- Start traversing forward step %s ---", stepCounter), "");
    Log.trace(String.format("Node %s will be visited ", nextNodes.keySet()), "");

    if (nextNodes.isEmpty()) {
      visitor.abort();
      return false;
    }

    Map<WfNode, Set<BranchID>> newBranchMap = new HashMap<>();

    for (Map.Entry<WfNode, Set<BranchID>> entry : nextNodes.entrySet()) {

      for (BranchID currentBranch : entry.getValue()) {

        WfNode node = entry.getKey();

        visitedNodes.add(node);

        switch (node.getNodeType()) {
          case EVENT:
          case TASK:
          case AND_SPLIT:
          case XOR_MERGE:
          case OR_MERGE:
            currentBranch.addNode(node);
            for (WfNode suc : node.getSuccessors()) {
              addEntry(newBranchMap, suc, currentBranch);
            }
            break;

          case AND_MERGE:
            if (visitedNodes.containsAll(node.getPredecessors())) {
              currentBranch.addNode(node);
              for (WfNode suc : node.getSuccessors()) {
                addEntry(newBranchMap, suc, currentBranch);
              }
            } else {
              addEntry(newBranchMap, node, currentBranch);
            }

            break;

          case XOR_SPLIT:
            currentBranch.addNode(node);
            for (WfNode suc : node.getSuccessors()) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              addEntry(newBranchMap, suc, newBranch);
            }
            currentBranch.setAborted();
            break;

          case OR_SPLIT:
            Set<? extends Set<? extends WfNode>> sucPowerSet = Sets.powerSet(node.getSuccessors());
            currentBranch.addNode(node);

            for (var sucSubset : sucPowerSet) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              for (WfNode suc : sucSubset) {
                addEntry(newBranchMap, suc, newBranch);
              }
            }
            currentBranch.setAborted();
            break;
        }
      }
    }

    Log.trace(String.format("--- End traversing forward step %s ---", stepCounter), "");

    Log.trace(String.format("--- Checking Conformance for step %s ---", stepCounter), "");

    nextNodes.values().stream().flatMap(Set::stream).forEach(visitor::accept);
    nextNodes.clear();
    nextNodes.putAll(newBranchMap);

    stepCounter++;
    return true;
  }

  public boolean stepBackward() {
    Log.println("");
    Log.trace(String.format("--- Start traversing backward step %s ---", stepCounter), "");
    Log.trace(String.format("Node %s will be visited ", nextNodes.keySet()), "");

    if (nextNodes.isEmpty()) {
      visitor.abort();
      return false;
    }

    Map<WfNode, Set<BranchID>> newBranchMap = new HashMap<>();

    for (Map.Entry<WfNode, Set<BranchID>> entry : nextNodes.entrySet()) {

      for (BranchID currentBranch : entry.getValue()) {

        WfNode node = entry.getKey();

        switch (node.getNodeType()) {
          case EVENT:
          case TASK:

          case AND_MERGE:
          case XOR_SPLIT:
          case XOR_MERGE:
          case OR_SPLIT:
            currentBranch.addNode(node);
            for (WfNode suc : node.getPredecessors()) {
              addEntry(newBranchMap, suc, currentBranch);
            }
            break;

          case AND_SPLIT:
            if (visitedNodes.containsAll(node.getSuccessors())) {
              currentBranch.addNode(node);
              for (WfNode suc : node.getPredecessors()) {
                addEntry(newBranchMap, suc, currentBranch);
              }
            } else {
              addEntry(newBranchMap, node, currentBranch);
            }
            break;

          case OR_MERGE:
            Log.error(String.format("Not implemented", "Backward", OR_MERGE));
            assert false;
        }
      }
    }

    Log.trace(String.format("--- End traversing backward step %s ---", stepCounter), "");

    Log.trace(String.format("--- Checking Conformance for step %s ---", stepCounter), "");
    nextNodes.values().stream().flatMap(Set::stream).forEach(visitor::accept);
    nextNodes.clear();
    nextNodes.putAll(newBranchMap);

    stepCounter++;
    return true;
  }

  private void addEntry(Map<WfNode, Set<BranchID>> branchMap, WfNode node, BranchID branchID) {
    if (branchMap.containsKey(node)) {
      branchMap.get(node).add(branchID);
    } else {
      branchMap.put(node, new HashSet<>(Set.of(branchID)));
    }
  }
}
