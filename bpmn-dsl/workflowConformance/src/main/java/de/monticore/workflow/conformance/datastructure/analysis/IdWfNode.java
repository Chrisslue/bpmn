package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Predicate;

public class IdWfNode implements WfNode {
  private Set<IdWfNode> predecessors = Collections.unmodifiableSet(new HashSet<>());
  private Set<IdWfNode> successors = Collections.unmodifiableSet(new HashSet<>());
  private final String label;
  private final NodeType nodeType;

  public static Set<IdWfNode> allNodes = Collections.unmodifiableSet(new HashSet<>());

  public Set<IdWfNode> getPredecessors() {
    return predecessors;
  }

  public Set<IdWfNode> getSuccessors() {
    return successors;
  }

  private IdWfNode(String label, NodeType nodeType) {
    this.label = label;
    this.nodeType = nodeType;



  }

  public static IdWfNode mkNode(String label, NodeType nodeType){
    return  getNode(label).orElse(new IdWfNode(label,nodeType));
  }

  public   static Optional<IdWfNode> getNode(String label){
   return allNodes.stream().filter(node->node.label.equals(label)).findAny();
  }

  public NodeType getNodeType() {
    return nodeType;
  }

   public void addPredecessor(Set<IdWfNode> predecessor){
     this.predecessors = Collections.unmodifiableSet(predecessor);
   }
  public void addSuccessors(,Set<IdWfNode> successor){
    this.successors = Collections.unmodifiableSet(successor);
  }

  @Override
  public String getLabel() {
    return label;
  }

  public Set<IdWfNode> getAllNodes() {
    return allNodes;
  }

  private Predicate<List<WfNode>> increasePath(
      Predicate<List<WfNode>> predicate, IdWfNode node) {
    return path -> {
      List<WfNode> newPath = new ArrayList<>(path);
      newPath.add(this);
      return predicate.test(newPath);
    };
  }

  public void setAllNodes(Set<IdWfNode> allNodes) {
    this.allNodes = Collections.unmodifiableSet(allNodes);
  }

  @Override
  public Optional<WfNode> existsPredecessor(Predicate<List<WfNode>> predicate, int searchDepth) {
    /* // todo correctly handle termination when search deep == -1

    for (ConfWfNode pred : this.predecessors) {

      switch (pred.nodeType) {
        case XOR_SPLIT:
          Optional<WfNode> res = pred.existsPredecessor(increasePath(predicate, pred), searchDepth);
          if (res.isPresent()) {
            return res;
          }
          break;

        case XOR_MERGE:
          res = pred.existsPredecessor(increasePath(predicate, pred), searchDepth);
          if (res.isPresent()) {
            return res;
          }
        default:
          if (predicate.test(List.of(pred))) {
            return Optional.of(pred);
          }
      }
    }

    if (searchDepth == 1 || searchDepth == -1) {
      return Optional.empty();
    }

    return this.existsPredecessor(increasePath(predicate, this), searchDepth - 1);*/
    assert false;
    Log.error("getting all predecessor is not yet implemented");
    return Optional.empty();
  }

  @Override
  public Optional<IdWfNode> existsSuccessor(
      Predicate<List<WfNode>> predicate, int searchDepth) {
    /* // todo correctly handle termination when search deep == -1

    for (ConfWfNode suc : this.successors) {

      switch (suc.nodeType) {
        case XOR_SPLIT:
          Optional<ConfWfNode> res = suc.existsSuccessor(increasePath(predicate, suc), searchDepth);
          if (res.isPresent()) {
            return res;
          }

          break;

        case XOR_MERGE:
          res = suc.existsSuccessor(increasePath(predicate, suc), searchDepth);
          if (res.isPresent()) {
            return res;
          }

          break;
        default:
          if (predicate.test(List.of(this, suc))) {
            return Optional.of(suc);
          }
          break;
      }
    }

    if (searchDepth == 1 || searchDepth == -1) {
      return Optional.empty();
    }

    return existsSuccessor(increasePath(predicate, this), searchDepth - 1);*/
    assert false;
    Log.error("exists all predecessor is not yet implemented");
    return Optional.empty();
  }

  @Override
  public Set<WfNode> allPredecessor(Predicate<List<WfNode>> predicate, int searchDepth) {

    /* if (searchDepth == 1) {
      Set<WfNode> res = new HashSet<>();

      for (ConfWfNode pred : predecessors) {
        if (predicate.test(List.of(this, pred))) {
          res.add(pred);
        }
      }
      return res;
    }*/
    assert false;
    Log.error("getting all predecessor is not yet implemented");

    return null;
  }

  @Override
  public Set<IdWfNode> allSuccessors(Predicate<List<WfNode>> predicate, int searchDepth) {

    /*  if (searchDepth == 1) {
      Set<ConfWfNode> res = new HashSet<>();

      for (ConfWfNode suc : successors) {

        switch (suc.nodeType) { // in the case the suc is a gateway
          case XOR_SPLIT:
            res.addAll(suc.allSuccessors(predicate, searchDepth));
            break;

          default:
            if (predicate.test(List.of(this, suc))) {
              res.add(suc);
            }
            break;
        }
      }
      return res;
    }*/
    assert false;
    Log.error("getting all successor is not yet implemented");

    return null;
  }

  public boolean isGateway() {
    return Set.of(
            NodeType.AND_SPLIT,
            NodeType.OR_SPLIT,
            NodeType.XOR_SPLIT,
            NodeType.OR_MERGE,
            NodeType.AND_MERGE,
            NodeType.XOR_MERGE)
        .contains(nodeType);
  }

  @Override
  public String toString() {
    return label;
  }
}
