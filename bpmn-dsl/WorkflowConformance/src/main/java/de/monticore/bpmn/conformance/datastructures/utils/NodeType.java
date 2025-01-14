package de.monticore.bpmn.conformance.datastructures.utils;

public enum NodeType {
  TASK(false),
  EVENT(false),
  AND_SPLIT(true),
  AND_MERGE(true),
  OR_SPLIT(true),
  OR_MERGE(true),
  XOR_MERGE(true),
  XOR_SPLIT(true),
  ;

  private final boolean isGateway;

  NodeType(boolean isGateway) {
    this.isGateway = isGateway;
  }

  public boolean isGateway() {
    return isGateway;
  }
}
