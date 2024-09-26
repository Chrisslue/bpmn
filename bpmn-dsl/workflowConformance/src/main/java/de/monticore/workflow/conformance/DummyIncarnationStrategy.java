package de.monticore.workflow.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;

public class DummyIncarnationStrategy {

  public boolean isIncarnation(WfNode concrete, WfNode reference) {
    return concrete.getLabel().equals(reference.getLabel());
  }
}
