package de.monticore.workflow.conformance;

import de.monticore.workflow.conformance.datastructure.BPMNBuilder;
import de.monticore.workflow.conformance.datastructure.BPMNNode;
import de.monticore.workflow.conformance.datastructure.BPMNNodeType;
import java.util.List;
import java.util.Optional;

public class LTSBuilder implements BPMNBuilder<BPMNNode> {
  @Override
  public BPMNNode mkTask(String name) {
    return new BPMNNode(name, BPMNNodeType.TASK);
  }

  @Override
  public BPMNNode mkEvent(String name) {
    return new BPMNNode(name, BPMNNodeType.EVENT);
  }

  @Override
  public BPMNNode mkXor(Optional<String> name, List<BPMNNode> bpmnNodes) {
    String n = name.orElse(null);
    return new BPMNNode(n, BPMNNodeType.XOR, bpmnNodes);
  }

  @Override
  public BPMNNode mkSequence(List<BPMNNode> nodeList) {
    return BPMNNode.mkSequence(nodeList);
  }

  @Override
  public BPMNNode mkAnd(Optional<String> name, List<BPMNNode> nodeList) {
    return new BPMNNode(name.orElse(null), BPMNNodeType.AND, nodeList);
  }

  @Override
  public BPMNNode mkOr(Optional<String> name, List<BPMNNode> nodeList) {
    return new BPMNNode(name.orElse(null), BPMNNodeType.OR, nodeList);
  }

  @Override
  public BPMNNode mkLoop(String name, BPMNNode forward, BPMNNode backward) {
    return null;
  }
}
