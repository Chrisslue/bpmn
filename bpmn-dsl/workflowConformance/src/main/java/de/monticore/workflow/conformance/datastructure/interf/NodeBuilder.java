package de.monticore.workflow.conformance.datastructure.interf;

public interface NodeBuilder<Node> {
  NodeBuilder<Node> addPredecessor(NodeBuilder<Node>... t); // returns "this", to allow chaining
  NodeBuilder<Node> addPredecessor(NodeBuilder<Node> t); // returns "this", to allow chaining
  NodeBuilder<Node> addSuccessor(NodeBuilder<Node>... t); // returns "this", to allow chaining
  NodeBuilder<Node> addSuccessor(NodeBuilder<Node> t); // returns "this", to allow chaining
}
