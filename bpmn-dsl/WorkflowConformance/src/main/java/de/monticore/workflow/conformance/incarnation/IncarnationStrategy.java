package de.monticore.workflow.conformance.incarnation;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.List;

public interface IncarnationStrategy {

  /**
   * Returns a list of all elements that match srcElem. The set of elements to check is provided by
   * the constructor of the implementation.
   */
  List<WfNode> getReferenceElements(WfNode srcElem);

  /** @return true iff srcElem and tgtElem match. */
  boolean isIncarnation(WfNode srcElem, WfNode tgtElem);
}
