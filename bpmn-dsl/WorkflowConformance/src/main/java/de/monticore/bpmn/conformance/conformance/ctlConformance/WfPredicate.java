package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.List;
import java.util.function.Predicate;

public class WfPredicate {
  private String predicateString;
  private Predicate<List<WfNode>> predicate;

  public WfPredicate(Predicate<List<WfNode>> predicate, String predicateString) {
    this.predicateString = predicateString;
    this.predicate = predicate;
  }

  public boolean test(List<WfNode> nodes) {
    return predicate.test(nodes);
  }

  public Predicate<List<WfNode>> getPredicate() {
    return predicate;
  }

  @Override
  public String toString() {
    return predicateString;
  }
}
