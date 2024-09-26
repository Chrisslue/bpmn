package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.utils.WfNodeCollector;
import java.util.HashMap;
import java.util.Map;

/***
 * construct a workflow graph form the BPMN AST.
 */
public class WfNodeGenerator {
  Map<String, NodeBuilder<ConfWfNode>> flowElements = new HashMap<>();

  ConfWfBuilder builder = new ConfWfBuilder();

  public ConfWfNode generateNode(ASTWorkflowCompilationUnit ast) {

    // traverse the Workflow ast a collect elements
    WfNodeCollector collector = new WfNodeCollector();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    // in the first pass first create vertices  and add them to the graph
    // Loop through tasks
    for (ASTTask t : collector.getTasks()) {
      flowElements.put(t.getName(), builder.mkNamedTask(t.getName()));
    }

    // Loop through named events
    for (ASTNamedEvent e : collector.getNamedEvents()) {
      flowElements.put(e.getName(), builder.mkNamedEvent(e.getName()));
    }

    // Loop through gateways
    for (ASTGateway g : collector.getGateways()) {
      flowElements.put(
          g.getName(), builder.mkNamedGateway(g.getName(), getGatewayType(g))); // todo fix type
    }

    for (ASTSequenceFlow sequenceFlow : collector.getSequenceFlows()) {
      for (int i = 0; i < sequenceFlow.getPathList().size() - 1; i++) {
        NodeBuilder<ConfWfNode> src =
            flowElements.get(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName());
        NodeBuilder<ConfWfNode> target =
            flowElements.get(sequenceFlow.getPathList().get(i + 1).getNodeRef().getBaseName());
        src.addSuccessor(target);
        target.addPredecessor(src);
      }
    }

    return flowElements.get(collector.getStartEvent().getName()).build();
  }

  protected NodeType getGatewayType(ASTGateway gateway) {
    boolean isMerge = gateway.getDirection().name().equals("MERGE");

    if (gateway.getType().isExclusive()) {
      return isMerge ? NodeType.XOR_MERGE : NodeType.XOR_SPLIT;
    } else if (gateway.getType().isInclusive()) {
      return isMerge ? NodeType.OR_MERGE : NodeType.OR_SPLIT;
    } else {
      return isMerge ? NodeType.AND_MERGE : NodeType.AND_SPLIT;
    }
  }
}
