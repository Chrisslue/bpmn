package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Predicate;

public class ConfWfNode implements WfNode {
  private List<ConfWfNode> predecessors = Collections.unmodifiableList(new ArrayList<>());
  private List<ConfWfNode> successors = Collections.unmodifiableList(new ArrayList<>());
  private final String label;
  private final NodeType nodeType;

  public ConfWfNode(String label, NodeType nodeType) {
    this.label = label;
    this.nodeType = nodeType;
  }

  public void addPredecessors(List<ConfWfNode> predecessor) {
    this.predecessors = Collections.unmodifiableList(predecessor);
  }

  public void addSuccessors(List<ConfWfNode> successor) {
    this.successors = Collections.unmodifiableList(successor);
  }

  @Override
  public String getLabel() {
    return label;
  }

  private Predicate<List<WfNode>> increasePath(Predicate<List<WfNode>> predicate, ConfWfNode node) {
    return path -> {
      List<WfNode> newPath = new ArrayList<>(path);
      newPath.add(this);
      return predicate.test(newPath);
    };
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
  public Optional<ConfWfNode> existsSuccessor(Predicate<List<WfNode>> predicate, int searchDepth) {
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
  public Set<ConfWfNode> allSuccessors(Predicate<List<WfNode>> predicate, int searchDepth) {

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
}
