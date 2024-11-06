package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfBuilder;
import java.util.*;

/***
 * this class contains methods that transform Workflow elements(tasks, gateways, events, etc. )
 * to workflow nodes.
 * It also collects sequence flow in the workflow in other to build transitions between nodes.
 */
public class ConfWfBuilder implements WfBuilder<IdWfNode> {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();

  private IdWfNode startEvent;
  private final String prefix;

  public ConfWfBuilder(String prefix) {
    this.prefix = prefix;
  }

  private String addPrefix(String name) {
    return prefix + name;
  }

  @Override
  public IdWfNode mkNamedTask(String name) {
      return IdWfNode.mkNode(addPrefix(name), NodeType.TASK);
  }

  @Override
  public IdWfNode mkNamedEvent(String name) {
return IdWfNode.mkNode(addPrefix(name), NodeType.EVENT);

  }

  @Override
  public IdWfNode mkNamedGateway(String name, NodeType type) {
   return IdWfNode.mkNode (addPrefix(name), type);

  }

  @Override
  public IdWfNode mkStartEvent(String label) {
    IdWfNode res = mkNamedEvent(label);
    startEvent = res;
    return res;
  }

  @Override
  public IdWfNode build() {



    Map<IdWfNode, Set<IdWfNode>> predecessors = new HashMap<>();
    Map<IdWfNode, Set<IdWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        IdWfNode src =
            IdWfNode.getNode(sequenceFlow.getPathList().get(i).getNodeRef()  .get(i).getNodeRef().getBaseName());
        IdWfNode tgt =
            IdWfNode.getNode(sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName());

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

      predecessors.forEach(IdWfNode::addPredecessors);
      successors.forEach(IdWfNode::addSuccessors);
    }
    return startEvent;
  }

  public void addSequenceFlow(ASTSequenceFlow sequenceFlow) {
    this.sequenceFlows.add(sequenceFlow);
  }
}
