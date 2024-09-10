package de.monticore.workflow.conformance.datastructure.interf;

public interface NodeBuilder<NodeType> {
  void addPredecessor(NodeBuilder<NodeType>... t);
}
