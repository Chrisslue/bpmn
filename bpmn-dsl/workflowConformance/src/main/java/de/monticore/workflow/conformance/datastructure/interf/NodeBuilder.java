package de.monticore.workflow.conformance.datastructure.interf;

public interface NodeBuilder<NodeType> {
  NodeBuilder<NodeType> addPredecessor(NodeBuilder<NodeType>... t); // returns "this", to allow chaining
  NodeBuilder<NodeType> addSuccessor(NodeBuilder<NodeType>... t); // returns "this", to allow chaining
}
