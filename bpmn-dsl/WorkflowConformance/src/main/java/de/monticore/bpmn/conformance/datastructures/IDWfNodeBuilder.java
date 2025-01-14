package de.monticore.bpmn.conformance.datastructures;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.NodeType;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import java.util.*;

/***
 * this class contains methods that transform Workflow elements (tasks, gateways, events, etc.)
 * to workflow nodes.
 * It also collects sequence flow in the workflow in other to build transitions between nodes.
 */
public class IDWfNodeBuilder implements WfBuilder {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private final Map<String, IDWfNode> allNodes = new HashMap<>();

  private final String prefix;

  public IDWfNodeBuilder(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public void mkNamedTask(ASTTask task) {
    ASTStereotype stereotype =
        task.getModifier().isPresentStereotype() ? task.getModifier().getStereotype() : null;
    mkNode(task.getName(), NodeType.TASK, stereotype, false, false);
  }

  @Override
  public void mkNamedEvent(ASTNamedEvent event) {
    ASTStereotype stereotype =
        event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype() : null;

    mkNode(event.getName(), NodeType.EVENT, stereotype, false, false);
  }

  @Override
  public void mkNamedGateway(ASTNamedGateway gateway) {
    NodeType nodeType;
    boolean isMerge = gateway.getDirection().name().equals("MERGE");

    if (gateway.getType().isExclusive()) {
      nodeType = isMerge ? NodeType.XOR_MERGE : NodeType.XOR_SPLIT;
    } else if (gateway.getType().isInclusive()) {
      nodeType = isMerge ? NodeType.OR_MERGE : NodeType.OR_SPLIT;
    } else {
      nodeType = isMerge ? NodeType.AND_MERGE : NodeType.AND_SPLIT;
    }

    mkNode(gateway.getName(), nodeType, null, false, false);
  }

  @Override
  public void mkStartEvent(ASTNamedEvent event) {
    ASTStereotype stereotype =
        event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype() : null;

    mkNode(event.getName(), NodeType.EVENT, stereotype, true, false);
  }

  @Override
  public void mkEndEvent(ASTNamedEvent event) {
    ASTStereotype stereotype =
        event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype() : null;

    mkNode(event.getName(), NodeType.EVENT, stereotype, false, true);
  }

  @Override
  public void mkSequence(ASTSequenceFlow sequenceFlow) {
    sequenceFlows.add(sequenceFlow);
  }

  @Override
  public IDWfNode getWfNode(String label) {

    IDWfNode res = allNodes.getOrDefault(label, null);

    if (res == null) {
      Log.error("Trying to get node " + label + " from Node builder but this node  is not present");
      assert false;
    }
    return res;
  }

  @Override
  public Set<WfNode> getAllNodes() {
    return new HashSet<>(allNodes.values());
  }

  private void mkNode(
      String label, NodeType nodeType, ASTStereotype stereotype, boolean isStart, boolean isEnd) {

    String newLabel = addPrefix(label);
    if (!allNodes.containsKey(label)) {
      IDWfNode res = new IDWfNode(newLabel, nodeType, stereotype, isStart, isEnd);
      allNodes.put(label, res);
    }
  }

  private String addPrefix(String name) {
    return this.prefix + name;
  }

  @Override
  public void build() {

    Map<IDWfNode, Set<IDWfNode>> predecessors = new HashMap<>();
    Map<IDWfNode, Set<IDWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        IDWfNode src = getWfNode(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName());
        IDWfNode tgt = getWfNode(sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName());

        if (successors.containsKey(src)) {
          successors.get(src).add(tgt);
        } else {
          successors.put(src, new HashSet<>(List.of(tgt)));
        }

        if (predecessors.containsKey(tgt)) {
          predecessors.get(tgt).add(src);
        } else {
          predecessors.put(tgt, new HashSet<>(List.of(src)));
        }
      }
    }
    predecessors.forEach(IDWfNode::addAllPredecessors);
    successors.forEach(IDWfNode::addAllSuccessors);
    System.out.println();
  }
}
