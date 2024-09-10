package de.monticore.workflow.conformance.datastructure.interf;

public interface BPMNBuilder<NodeType> {
  NodeBuilder<NodeType> andNodeBuilder();
  NodeBuilder<NodeType> xorNodeBuilder();
  NodeBuilder<NodeType> namedTaskNodeBuilder(String name);
}
