package de.monticore.workflow.conformance.datastructure.interf;

import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.utils.NodeType;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Function;

/***
 * this class contains methods that transform Workflow elements(tasks, gateways, events, etc. )
 * to workflow nodes.
 * It also collects sequence flow in the workflow in other to build transitions between nodes.
 */
public class IDWfNodeBuilder implements WfBuilder<IDWfNode> {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private final Set<IDWfNode> allNodes = new HashSet<>();
  private boolean isBuild = false;
  // todo manage the list of start events
  private final Function<String, String> identifier;

  public Set<IDWfNode> getAllNodes() {

    return allNodes;
  }

  public IDWfNodeBuilder(Function<String, String> identifier) {
    this.identifier = identifier;
  }

  @Override
  public void mkNamedTask(String name) {
    mkNode(addPrefix(name), NodeType.TASK);
  }

  @Override
  public void mkNamedEvent(String name) {
    mkNode(addPrefix(name), NodeType.EVENT);
  }

  @Override
  public void mkNamedGateway(String name, NodeType type) {
    mkNode(addPrefix(name), type);
  }

  @Override
  public void mkStartEvent(String label) {
    mkNamedEvent(label);
  }

  public IDWfNode getNode(String label) {

    Optional<IDWfNode> res =
        allNodes.stream().filter(node -> node.getLabel().equals(label)).findAny();

    if (res.isEmpty()) {
      Log.error(
          String.format(
              "Trying to get node %s from Node builder but this node  is not present", label));
      assert false;
    }
    return res.get();
  }

  private void mkNode(String label, NodeType nodeType) {
    if (allNodes.stream().anyMatch(node -> node.getLabel().equals(label))) {
      getNode(label);
      return;
    }
    IDWfNode res = new IDWfNode(label, nodeType);
    allNodes.add(res);
  }

  private String addPrefix(String name) {
    return identifier.apply(name);
  }

  public void build() {

    Map<IDWfNode, Set<IDWfNode>> predecessors = new HashMap<>();
    Map<IDWfNode, Set<IDWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        IDWfNode src =
            getNode(identifier.apply(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName()));
        IDWfNode tgt =
            getNode(
                identifier.apply(sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName()));

        if (successors.containsKey(src)) {
          successors.get(src).add(tgt);
        } else {
          successors.put(src, new HashSet<>(List.of(tgt)));
        }

        if (predecessors.containsKey(tgt)) {
          predecessors.get(tgt).add(src);
        } else {
          predecessors.put(src, new HashSet<>(List.of(src)));
        }
      }

      predecessors.forEach(IDWfNode::addAllPredecessors);
      successors.forEach(IDWfNode::addAllSuccessors);
    }
    isBuild = true;
  }

  public void addSequenceFlow(ASTSequenceFlow sequenceFlow) {
    this.sequenceFlows.add(sequenceFlow);
  }
}
