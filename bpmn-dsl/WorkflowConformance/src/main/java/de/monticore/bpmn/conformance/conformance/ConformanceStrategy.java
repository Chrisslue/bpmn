package de.monticore.bpmn.conformance.conformance;

import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;

public interface ConformanceStrategy<NODE> {

  CheckResult checkConformance(NODE concrete);
}
