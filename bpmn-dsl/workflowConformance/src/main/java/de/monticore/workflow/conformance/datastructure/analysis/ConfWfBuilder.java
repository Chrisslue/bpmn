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
public class ConfWfBuilder implements WfBuilder<ConfWfNode> {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();
  private final Map<String, ConfWfNode> wfNodesMap = new HashMap<>();
  private ConfWfNode startEvent;
  private final String prefix;

  public ConfWfBuilder(String prefix) {
    this.prefix = prefix;
  }

  private String addPrefix(String name) {
    return prefix + name;
  }

  @Override
  public ConfWfNode mkNamedTask(String name) {
    ConfWfNode res = new ConfWfNode(addPrefix(name), NodeType.TASK);
    wfNodesMap.put(name, res);
    return res;
  }

  @Override
  public ConfWfNode mkNamedEvent(String name) {
    ConfWfNode res = new ConfWfNode(addPrefix(name), NodeType.EVENT);
    wfNodesMap.put(name, res);
    return res;
  }

  @Override
  public ConfWfNode mkNamedGateway(String name, NodeType type) {
    ConfWfNode res = new ConfWfNode(addPrefix(name), type);
    wfNodesMap.put(name, res);
    return res;
  }

  @Override
  public ConfWfNode mkStartEvent(String label) {
    ConfWfNode res = mkNamedEvent(label);
    startEvent = res;
    return res;
  }

  @Override
  public ConfWfNode build() {

    Map<ConfWfNode, List<ConfWfNode>> predecessors = new HashMap<>();
    Map<ConfWfNode, List<ConfWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        ConfWfNode src =
            wfNodesMap.get(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName());
        ConfWfNode tgt =
            wfNodesMap.get(sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName());

        if (successors.containsKey(src)) {
          successors.get(src).add(tgt);
        } else {
          successors.put(src, new ArrayList<>(List.of(tgt)));
        }

        if (predecessors.containsKey(tgt)) {
          predecessors.get(tgt).add(src);
        } else {
          predecessors.put(src, new ArrayList<>(List.of(src)));
        }
      }

      predecessors.forEach(ConfWfNode::addPredecessors);
      successors.forEach(ConfWfNode::addSuccessors);
    }
    return startEvent;
  }

  public void addSequenceFlow(ASTSequenceFlow sequenceFlow) {
    this.sequenceFlows.add(sequenceFlow);
  }
}
