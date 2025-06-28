/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.conformance.ctlConformance;

import com.google.common.collect.Sets;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import de.se_rwth.commons.logging.Log;
import java.util.*;

/****
 * This class is used to traverse the BPMN using Breadth-First Search!
 * The traversal is performed step by step in a set of branches executed in parallel.
 * In each branch, there is a set of nodes that are active.
 * In each step, all the active nodes of all branches are visited.
 * *
 * nextNode: contains the set of active nodes and the branch where each node is active.
 * visitedNode: contains all the nodes that were visited in each branch.
 */

public class BFSConfWfTraverser {
  
  private final Map<WfNode, Set<BranchID>> nextNodes = new HashMap<>();
  private final Map<BranchID, Set<WfNode>> visitedNodes = new HashMap<>();
  private final BranchVisitor visitor;
  private final WfNode branchOrigin;
  
  private int stepCounter = 0;
  
  public BFSConfWfTraverser(BranchVisitor wfVisitor, WfNode branchOrigin) {
    this.visitor = wfVisitor;
    nextNodes.put(branchOrigin, new HashSet<>(List.of(new BranchID(new ArrayList<>()))));
    this.branchOrigin = branchOrigin;
  }
  
  public boolean stepForward() {
    
    Log.trace("", "");
    Log.trace(String.format("--- Start traversing forward step %s ---", stepCounter), "");
    Log.trace(String.format("Node %s will be visited ", nextNodes.keySet()), "");
    
    Set<BranchID> abortedBranch = new HashSet<>();
    
    // case they are no nodes to visit
    if (nextNodes.isEmpty()) {
      visitor.abort();
      return false;
    }
    
    Map<WfNode, Set<BranchID>> newBranchMap = new HashMap<>();
    
    for (Map.Entry<WfNode, Set<BranchID>> entry : nextNodes.entrySet()) {
      for (BranchID currentBranch : entry.getValue()) {
        
        WfNode node = entry.getKey();
        updateVisitedNode(node, currentBranch);
        
        // we don't want to add the branch origin in branch id in the first step.
        if (stepCounter != 0 || !node.equals(branchOrigin)) {
          currentBranch.addNode(node);
        }
        
        // ideally, this situation should not occur but if so we stop
        // when the current branch is either aborted or when the check is completed.
        if (currentBranch.isCheckCompleted() || currentBranch.isCheckAborted()) {
          break;
        }
        
        switch (node.getNodeType()) {
          case EVENT:
          case TASK:
          case AND_SPLIT:
          case XOR_MERGE:
          case OR_MERGE:
            node.getSuccessors().forEach(suc -> addNode2ActiveNodesMap(newBranchMap, suc,
                currentBranch));
            break;
          
          case AND_MERGE:
            // we merge in the case were we visited all the predecessors of the AND-MERGE node
            // or when there is only one active node in the current branch
            if (visitedNodes.get(currentBranch).containsAll(node.getPredecessors())
                || getActiveNodesInBranch(currentBranch).size() == 1) {
              
              currentBranch.merge(node);
              
              for (WfNode suc : node.getSuccessors()) {
                addNode2ActiveNodesMap(newBranchMap, suc, currentBranch);
              }
              
            }
            else {
              // wait
              currentBranch.wait(node);
              addNode2ActiveNodesMap(newBranchMap, node, currentBranch);
            }
            
            break;
          
          case XOR_SPLIT:
            // create new branches form the original one and abort the original one
            for (WfNode suc : node.getSuccessors()) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              addNode2ActiveNodesMap(newBranchMap, suc, newBranch);
            }
            currentBranch.abortCheck();
            break;
          
          case OR_SPLIT:
            // create the power
            Set<? extends Set<? extends WfNode>> sucPowerSet = Sets.powerSet(node.getSuccessors());
            
            for (var sucSubset : sucPowerSet) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              for (WfNode suc : sucSubset) {
                addNode2ActiveNodesMap(newBranchMap, suc, newBranch);
              }
            }
            currentBranch.abortCheck();
            break;
        }
      }
    }
    
    Log.trace(String.format("--- End traversing forward step %s ---", stepCounter), "");
    Log.trace(String.format("--- Checking Conformance for step %s ---", stepCounter), "");
    
    nextNodes.values().stream().flatMap(Set::stream).forEach(branchID -> {
      if (!visitor.accept(branchID)) {
        abortedBranch.add(branchID);
      }
    });
    
    removeAbortedBranches(newBranchMap, abortedBranch);
    nextNodes.clear();
    nextNodes.putAll(newBranchMap);
    
    stepCounter++;
    return true;
  }
  
  public boolean stepBackward() {
    Log.trace("", "");
    Log.trace(String.format("--- Start traversing backward step %s ---", stepCounter), "");
    Log.trace(String.format("Node %s will be visited ", nextNodes.keySet()), "");
    
    if (nextNodes.isEmpty()) {
      visitor.abort();
      return false;
    }
    
    Map<WfNode, Set<BranchID>> newBranchMap = new HashMap<>();
    Set<BranchID> abortedBranch = new HashSet<>();
    for (Map.Entry<WfNode, Set<BranchID>> entry : nextNodes.entrySet()) {
      
      for (BranchID currentBranch : entry.getValue()) {
        
        WfNode node = entry.getKey();
        
        switch (node.getNodeType()) {
          case EVENT:
          case TASK:
          case AND_MERGE:
          case XOR_SPLIT:
          case OR_SPLIT:
            currentBranch.addNode(node);
            for (WfNode suc : node.getPredecessors()) {
              addNode2ActiveNodesMap(newBranchMap, suc, currentBranch);
            }
            break;
          
          case AND_SPLIT:
            if ((visitedNodes.containsKey(currentBranch) && visitedNodes.get(currentBranch)
                .containsAll(node.getSuccessors())) || getActiveNodesInBranch(currentBranch).size()
                    == 1) {
              currentBranch.addNode(node);
              for (WfNode suc : node.getPredecessors()) {
                addNode2ActiveNodesMap(newBranchMap, suc, currentBranch);
              }
            }
            else {
              addNode2ActiveNodesMap(newBranchMap, node, currentBranch);
            }
            break;
          
          case OR_MERGE:
            Set<? extends Set<? extends WfNode>> sucPowerSet = Sets.powerSet(node
                .getPredecessors());
            
            for (var sucSubset : sucPowerSet) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              for (WfNode suc : sucSubset) {
                addNode2ActiveNodesMap(newBranchMap, suc, newBranch);
              }
            }
            currentBranch.abortCheck();
            break;
          
          case XOR_MERGE:
            for (WfNode suc : node.getPredecessors()) {
              BranchID newBranch = new BranchID(new ArrayList<>(currentBranch.getNodeList()));
              addNode2ActiveNodesMap(newBranchMap, suc, newBranch);
            }
            currentBranch.abortCheck();
            break;
        }
      }
    }
    
    Log.trace(String.format("--- End traversing backward step %s ---", stepCounter), "");
    Log.trace(String.format("--- Checking Conformance for step %s ---", stepCounter), "");
    
    // collect aborted branches
    nextNodes.values().stream().flatMap(Set::stream).forEach(branchID -> {
      if (!visitor.accept(branchID)) {
        abortedBranch.add(branchID);
      }
    });
    
    // remove aborted branches from the map contains next active nodes
    removeAbortedBranches(newBranchMap, abortedBranch);
    
    // update the active nodes for the next steps
    nextNodes.clear();
    nextNodes.putAll(newBranchMap);
    
    stepCounter++;
    return true;
  }
  
  private void removeAbortedBranches(Map<WfNode, Set<BranchID>> branchMap,
      Set<BranchID> abortedBranches) {
    
    Map<WfNode, Set<BranchID>> copy = new HashMap<>(branchMap);
    for (Map.Entry<WfNode, Set<BranchID>> entry : copy.entrySet()) {
      Set<BranchID> branches = entry.getValue();
      
      branches.removeIf(abortedBranches::contains);
      if (branches.isEmpty()) {
        branchMap.remove(entry.getKey());
      }
    }
  }
  
  private void addNode2ActiveNodesMap(Map<WfNode, Set<BranchID>> branchMap, WfNode node,
      BranchID branchID) {
    if (branchMap.containsKey(node)) {
      branchMap.get(node).add(branchID);
    }
    else {
      branchMap.put(node, new HashSet<>(Set.of(branchID)));
    }
  }
  
  private void updateVisitedNode(WfNode node, BranchID branchID) {
    if (visitedNodes.containsKey(branchID) && visitedNodes.get(branchID) != null) {
      visitedNodes.get(branchID).add(node);
    }
    else {
      visitedNodes.put(branchID, new HashSet<>(Set.of(node)));
    }
  }
  
  public Set<WfNode> getActiveNodesInBranch(BranchID branchID) {
    Set<WfNode> res = new HashSet<>();
    nextNodes.entrySet().stream().filter(entry -> entry.getValue().contains(branchID)).forEach(
        entry -> res.add(entry.getKey()));
    return res;
  }
  
}
