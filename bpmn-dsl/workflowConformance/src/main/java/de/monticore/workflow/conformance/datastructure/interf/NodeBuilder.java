package de.monticore.workflow.conformance.datastructure.interf;

public interface NodeBuilder<Node> {
  NodeBuilder<Node> addPredecessor(
      NodeBuilder<Node> predecessors); // returns "this", to allow chaining

  NodeBuilder<Node> addSuccessor(NodeBuilder<Node> successors); // returns "this", to allow chaining

  Node build();
}
