package de.monticore.bpmn.conformance.conformance.ctlConformance;

enum AbortReason {
  SATISFIED_PREDICATE,
  LOOP_DISCOVERED,
  END_NODE_REACHED,
  RETURN_TO_START
}
