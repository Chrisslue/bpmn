package de.monticore.workflow.conformance.datastructure.interf;

public interface BPMNBuilder<NodeType> {
  NodeBuilder<NodeType> parallelNodeBuilder();
  NodeBuilder<NodeType> xorNodeBuilder();
  NodeBuilder<NodeType> namedTaskNodeBuilder(String name);
}
