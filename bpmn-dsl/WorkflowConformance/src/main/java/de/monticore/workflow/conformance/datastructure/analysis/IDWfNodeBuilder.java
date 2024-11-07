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
public class IDWfNodeBuilder implements WfBuilder<IDWfNode> {
  private final Set<ASTSequenceFlow> sequenceFlows = new HashSet<>();

  public Set<IDWfNode> allNodes = new HashSet<>();

  public Optional<IDWfNode> getNode(String label) {
    return allNodes.stream().filter(node -> node.getLabel().equals(label)).findAny();
  }

  public IDWfNode mkNode(String label, NodeType nodeType) {
    if (getNode(label).isPresent()) {
      return getNode(label).get();
    }
    IDWfNode res = new IDWfNode(label, nodeType);
    allNodes.add(res);
    return res;
  }

  private IDWfNode startEvent;
  private final String prefix;

  public IDWfNodeBuilder(String prefix) {
    this.prefix = prefix;
  }

  private String addPrefix(String name) {
    return prefix + name;
  }

  @Override
  public IDWfNode mkNamedTask(String name) {
    return mkNode(addPrefix(name), NodeType.TASK);
  }

  @Override
  public IDWfNode mkNamedEvent(String name) {
    return mkNode(addPrefix(name), NodeType.EVENT);
  }

  @Override
  public IDWfNode mkNamedGateway(String name, NodeType type) {
    return mkNode(addPrefix(name), type);
  }

  @Override
  public IDWfNode mkStartEvent(String label) {
    IDWfNode res = mkNamedEvent(label);
    startEvent = res;
    return res;
  }

  @Override
  public IDWfNode build() {

    Map<IDWfNode, Set<IDWfNode>> predecessors = new HashMap<>();
    Map<IDWfNode, Set<IDWfNode>> successors = new HashMap<>();
    for (ASTSequenceFlow sequenceFlow : sequenceFlows) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {

        IDWfNode src =
            getNode(prefix + sequenceFlow.getPathList().get(i).getNodeRef().getBaseName())
                .get(); // todo fix
        IDWfNode tgt =
            getNode(prefix + sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName())
                .get();

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

      predecessors.forEach(IDWfNode::addPredecessors);
      successors.forEach(IDWfNode::addSuccessors);
    }
    return startEvent;
  }

  public void addSequenceFlow(ASTSequenceFlow sequenceFlow) {
    this.sequenceFlows.add(sequenceFlow);
  }
}
