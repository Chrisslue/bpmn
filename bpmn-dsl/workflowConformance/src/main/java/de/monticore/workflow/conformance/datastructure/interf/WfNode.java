package de.monticore.workflow.conformance.datastructure.interf;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {
  WfNodeType getNodeType();  // ... ungern ...
  String getLabel();    // toString() ? Mehr kommentare. z.B. "ist das Label eindeutig?"

  // ... maybe?
  Optional<WfNode> existsPredecessor(Predicate<WfNode> predicate, int searchDepth);
  Set<WfNode> allPredecessor(Predicate<WfNode> predicate, int searchDepth);
}
