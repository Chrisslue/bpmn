package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;

public class ConfWfNodeBuilder implements NodeBuilder<ConfWfNode> {

  private final ConfWfNode node;

  public ConfWfNodeBuilder(String label, NodeType nodeType) {
    node = new ConfWfNode(label, nodeType);
  }

  @Override
  public final NodeBuilder<ConfWfNode> addPredecessor(NodeBuilder<ConfWfNode> predecessor) {

    this.node.addPredecessor(predecessor.build());
    return this;
  }

  @Override
  public final NodeBuilder<ConfWfNode> addSuccessor(NodeBuilder<ConfWfNode> successor) {

    this.node.addSuccessor(successor.build());

    return this;
  }

  @Override
  public ConfWfNode build() {
    return node;
  }
}
