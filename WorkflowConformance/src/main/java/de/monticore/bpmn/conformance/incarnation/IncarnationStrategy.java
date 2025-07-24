/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.incarnation;

import java.util.List;

public interface IncarnationStrategy<NODE> {
  
  /**
   * Returns a list of all elements that match srcElem. The set of elements to check is provided by
   * the constructor of the implementation.
   */
  List<NODE> getReferenceElements(NODE srcElem);
  
  /** @return true iff srcElem and tgtElem match. */
  boolean isIncarnation(NODE srcElem, NODE tgtElem);
  
}
