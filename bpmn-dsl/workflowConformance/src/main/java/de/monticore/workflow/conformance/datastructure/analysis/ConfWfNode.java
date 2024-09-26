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
      BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
    return existsPredecessor(predicate, new ArrayList<>(), searchDepth);
  }

  public Optional<WfNode> existsPredecessor(
      BiPredicate<List<WfNode>, WfNode> predicate, List<WfNode> path, int searchDepth) {
    // todo correctly handle termination when search deep == -1

    for (ConfWfNode pred : this.predecessors) {

      if (predicate.test(path, pred)) {
        return Optional.of(pred);
      }
    }

    if (searchDepth == 1 || searchDepth == -1) {
      return Optional.empty();
    }
    List<WfNode> newPath = new ArrayList<>(path);
    newPath.add(this);
    return existsPredecessor(predicate, newPath, searchDepth - 1);
  }

  @Override
  public Optional<WfNode> existsSuccessor(
      BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
    return existsSuccessor(predicate, new ArrayList<>(), searchDepth);
  }

  public Optional<WfNode> existsSuccessor(
      BiPredicate<List<WfNode>, WfNode> predicate, List<WfNode> path, int searchDepth) {
    // todo correctly handle termination when search deep == -1

    for (ConfWfNode suc : this.successors) {

      if (predicate.test(path, suc)) {
        return Optional.of(suc);
      }
    }

    if (searchDepth == 1 || searchDepth == -1) {
      return Optional.empty();
    }
    List<WfNode> newPath = new ArrayList<>(path);
    newPath.add(this);
    return existsSuccessor(predicate, newPath, searchDepth - 1);
  }

  @Override
  public Set<WfNode> allPredecessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {

    if (searchDepth == 1) {
      Set<WfNode> res = new HashSet<>();

      for (ConfWfNode pred : predecessors) {
        if (predicate.test(List.of(this, pred), pred)) {
          res.add(pred);
        }
      }
      return res;
    }
    assert false;
    Log.error("getting all predecessor is not yet implemented");

    return null;
  }

  @Override
  public Set<WfNode> allSuccessors(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth) {
    if (searchDepth == 1) {
      Set<WfNode> res = new HashSet<>();

      for (ConfWfNode suc : successors) {
        if (predicate.test(List.of(this, suc), suc)) {
          res.add(suc);
        }
      }
      return res;
    }
    assert false;
    Log.error("getting all sucessor is not yet implemented");

    return null;
  }
}
