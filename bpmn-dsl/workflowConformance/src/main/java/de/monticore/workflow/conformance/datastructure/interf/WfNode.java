package de.monticore.workflow.conformance.datastructure.interf;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {

  String getLabel();

  /**
   * @param predicate List<WfNode> = Path to node X WfNode X predecessor reachable with <=
   *     searchDepth steps
   * @param searchDepth: -1 = unlimited search; 0 = no search;
   * @return Node X
   */
  Optional<? extends WfNode> existsPredecessor(Predicate<List<WfNode>> predicate, int searchDepth);

  Optional<? extends WfNode> existsSuccessor(Predicate<List<WfNode>> predicate, int searchDepth);

  Set<? extends WfNode> allPredecessor(Predicate<List<WfNode>> predicate, int searchDepth);

  Set<? extends WfNode> allSuccessors(Predicate<List<WfNode>> predicate, int searchDepth);
}
