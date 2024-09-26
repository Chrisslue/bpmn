package de.monticore.workflow.conformance.datastructure.interf;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {

  String getLabel(); // toString() ? Mehr kommentare. z.B. "ist das Label eindeutig?"

  // ... maybe?

  /**
   * @param predicate List<WfNode> = Path to node X WfNode X predecessor reachable with <=
   *     searchDepth steps
   * @param searchDepth: -1 = unlimited search; 0 = no search;
   * @return Node X
   */
  Optional< WfNode> existsPredecessor(BiPredicate<List< WfNode>, WfNode> predicate, int searchDepth);

  Optional<WfNode> existsSuccessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth);

  Set< WfNode> allPredecessor(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth);

  Set< WfNode> allSuccessors(BiPredicate<List<WfNode>, WfNode> predicate, int searchDepth);
}
