package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.BiPredicate;

public class ConfWfNode implements WfNode {
  private final List<ConfWfNode> predecessors = new ArrayList<>();
  private final List<ConfWfNode> successors = new ArrayList<>();

  private final String label;

  private final NodeType nodeType;

  public ConfWfNode(String label, NodeType nodeType) {
    this.label = label;
    this.nodeType = nodeType;
  }

  public void addPredecessor(ConfWfNode predecessor) {
    this.predecessors.add(predecessor);
  }

  public void addSuccessor(ConfWfNode successor) {
    this.successors.add(successor);
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public Optional<WfNode> existsPredecessor(
          BiPredicate<List<WfNode>,  WfNode> predicate, int searchDepth) {
    return Optional.empty();
  }

  @Override
  public Optional<WfNode> existsSuccessor(
      BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
    if (searchDepth == 1){
      for (ConfWfNode suc : this.successors){
        if (predicate.test(List.of(this,suc),suc)){
          return Optional.of(suc);
        }
      }

      return Optional.empty();
    }

    Log.error("exists Successor not fully implemented yet");
    return Optional.empty();
  }

  @Override
  public Set<WfNode> allPredecessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {

    if (searchDepth == 1){
      Set<WfNode> res = new HashSet<>();

      for(ConfWfNode pred :predecessors){
        if (predicate.test(List.of(this,pred),pred)){
          res.add(pred);
        }
      }
      return  res ;
    }

    Log.error("getting all predecessor is not yet implemented");
     return  null;
  }

  @Override
  public Set<WfNode> allSuccessors(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
    if (searchDepth == 1){
      Set<WfNode> res = new HashSet<>();

      for(ConfWfNode suc :successors){
        if (predicate.test(List.of(this,suc),suc)){
          res.add(suc);
        }
      }
      return  res ;
    }

    Log.error("getting all sucessor is not yet implemented");
    return  null;
  }
}
