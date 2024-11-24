package de.monticore.workflow.conformance.datastructure.interf;

import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.utils.NodeType;
import de.se_rwth.commons.logging.Log;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/***
 * this class contains methods that transform Workflow elements(tasks, gateways, events, etc. )
 * to workflow nodes.
 * It also collects sequence flow in the workflow in other to build transitions between nodes.
 */
public class IDWfNodeBuilder implements WfBuilder {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private final Set<IDWfNode> allNodes = new HashSet<>();

  private final Function<String, String> identifier;

  public Set<IDWfNode> getAllNodes() {
    return allNodes;
  }

  public IDWfNodeBuilder(Function<String, String> identifier) {
    this.identifier = identifier;
  }

  @Override
  public void mkNamedTask(String name) {
    mkNode(mkUniqueString(name), NodeType.TASK, false, false);
  }

  @Override
  public void mkNamedEvent(String name) {
    mkNode(mkUniqueString(name), NodeType.EVENT, false, false);
  }

  @Override
  public void mkNamedGateway(String name, NodeType type) {
    mkNode(mkUniqueString(name), type, false, false);
  }

  @Override
  public void mkStartEvent(String label) {
    mkNode(mkUniqueString(label), NodeType.EVENT, true, false);
  }

  @Override
  public void mkEndEvent(String label) {
    mkNode(mkUniqueString(label), NodeType.EVENT, false, true);
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

  private WfNode mkNode(String label, NodeType nodeType, boolean isStart, boolean isEnd) {
    if (allNodes.stream().anyMatch(node -> node.getLabel().equals(label))) {
      return getNode(label);
    }
    IDWfNode res = new IDWfNode(label, nodeType, isStart, isEnd);
    allNodes.add(res);
    return res;
  }

  private String mkUniqueString(String name) {
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
  }

  public void addSequenceFlow(ASTSequenceFlow sequenceFlow) {
    this.sequenceFlows.add(sequenceFlow);
  }

  public Set<WfNode> getStartNodes() {

    return allNodes.stream().filter(IDWfNode::isStart).collect(Collectors.toSet());
  }
}
