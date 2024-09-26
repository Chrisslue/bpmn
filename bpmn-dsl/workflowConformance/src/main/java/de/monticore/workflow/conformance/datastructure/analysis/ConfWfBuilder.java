package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfBuilder;
import java.util.List;
import java.util.Optional;

public class ConfWfBuilder implements WfBuilder<ConfWfNode> {
  @Override
  public NodeBuilder<ConfWfNode> mkNamedTask(String name) {
    return new ConfWfNodeBuilder(name, NodeType.TASK);
  }

  @Override
  public NodeBuilder<ConfWfNode> mkNamedEvent(String name) {
    return new ConfWfNodeBuilder(name, NodeType.TASK); // todo change to event
  }

  @Override
  public NodeBuilder<ConfWfNode> mkNamedGateway(String name, NodeType type) {
    return new ConfWfNodeBuilder(name, type);
  }

  @Override
  public NodeBuilder<ConfWfNode> mkXor(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
    return null;
  }

  @Override
  public NodeBuilder<ConfWfNode> mkSequence(List<NodeBuilder<ConfWfNode>> nodes) {
    return null;
  }

  @Override
  public NodeBuilder<ConfWfNode> mkAnd(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
    return null;
  }

  @Override
  public NodeBuilder<ConfWfNode> mkOr(Optional<String> name, List<NodeBuilder<ConfWfNode>> nodes) {
    return null;
  }

  @Override
  public NodeBuilder<ConfWfNode> mkLoop(
      String name, NodeBuilder<ConfWfNode> forward, NodeBuilder<ConfWfNode> backward) {
    return null;
  }
}
