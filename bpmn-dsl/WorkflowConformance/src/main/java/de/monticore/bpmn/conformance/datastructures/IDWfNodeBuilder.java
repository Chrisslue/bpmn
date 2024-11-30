package de.monticore.bpmn.conformance.datastructures;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.NodeType;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.umlstereotype._ast.ASTStereotype;
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



  public IDWfNodeBuilder(Function<String, String> identifier) {
    this.identifier = identifier;
  }

  @Override
  public void mkNamedTask(ASTTask task) {
    ASTStereotype stereotype  = task.getModifier().isPresentStereotype() ? task.getModifier().getStereotype():null;
    mkNode(mkUniqueString(task.getName()), NodeType.TASK, stereotype ,false, false);
  }

  @Override
  public void mkNamedEvent(ASTNamedEvent event) {
    ASTStereotype stereotype  = event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype():null;

    mkNode(mkUniqueString(event.getName()), NodeType.EVENT, stereotype,false, false);
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

    mkNode(mkUniqueString(gateway.getName()), nodeType,null ,false, false);
  }

  @Override
  public void mkStartEvent(
          ASTNamedEvent event
  ) {
    ASTStereotype stereotype  =  event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype():null;

    mkNode(mkUniqueString(event.getName()), NodeType.EVENT, stereotype,true, false);
  }

  @Override
  public void mkEndEvent(ASTNamedEvent event) {
    ASTStereotype stereotype  =  event.getModifier().isPresentStereotype() ? event.getModifier().getStereotype():null;


    mkNode(mkUniqueString(event.getName()), NodeType.EVENT, stereotype,false, true);
  }

  @Override
  public void mkSequence(ASTSequenceFlow sequenceFlow) {
    sequenceFlows.add(sequenceFlow);
  }

  @Override
  public IDWfNode resolveNode(String label) {
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

  @Override
  public Set<WfNode> getAllNodes() {
    return allNodes.stream().map(n->(WfNode)n).collect(Collectors.toSet());
  }


  private void mkNode(String label, NodeType nodeType, ASTStereotype stereotype , boolean isStart, boolean isEnd) {
    if (allNodes.stream().noneMatch(node -> node.getLabel().equals(label))) {
      IDWfNode res = new IDWfNode(label, nodeType, stereotype ,isStart, isEnd);
      allNodes.add(res);
    }

  }

  private String mkUniqueString(String name) {
    return identifier.apply(name);
  }


  @Override
  public void build() {

    Map<IDWfNode, Set<IDWfNode>> predecessors = new HashMap<>();
    Map<IDWfNode, Set<IDWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        IDWfNode src =
            resolveNode(identifier.apply(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName()));
        IDWfNode tgt =
            resolveNode(
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
}
