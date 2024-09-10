package de.monticore.workflow.conformance.datastructure.jwf;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.interf.WfGraph;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.datastructure.interf.WfNodeType;
import de.monticore.workflow.conformance.utils.WfNodeCollector;
import java.util.HashMap;
import java.util.Map;

/***
 * construct a workflow graph form the BPMN AST.
 */
public class WfGraphGenerator {
  Map<String, WfNode> flowElements = new HashMap<>();
  static  int counter = 0 ;

  public JwfGraph mkGraph(ASTWorkflowCompilationUnit ast) {
    JwfGraph graph = new JwfGraph();

    // traverse the Workflow ast a collect elements
    WfNodeCollector collector = new WfNodeCollector();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    // in the first pass first create vertices  and add them to the graph
    collector.getTasks().forEach(t -> graph.addNode(mkTaskNode(t)));
    collector.getNamedEvents().forEach(t -> graph.addNode(mkEventNode(t)));
    collector.getGateways().forEach(t -> graph.addNode(mkGatewayNode(t)));

    for (ASTSequenceFlow sequenceFlow : collector.getSequenceFlows()) {
      for (int i = 0; i < sequenceFlow.getPathList().size()-1; i++) {
        WfNode src = flowElements.get(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName());
        WfNode target = flowElements.get(sequenceFlow.getPathList().get(i+1).getNodeRef().getBaseName());

        graph.addEdge(src, target);
      }
    }

    return graph;
  }

  private WfNode mkGatewayNode(ASTGateway gateway) {
    String  label = "dummyLabel"+counter++ ;
    var node = new JwfGateway(WfNodeType.OR_SPLIT,label); // todo implement for other gateways
    flowElements.put(gateway.getName(), node);
    return node;
  }

  private WfNode mkEventNode(ASTNamedEvent event) {
    var node = new JwfTask(event.getName());
    flowElements.put(event.getName(), node);
    return node;
  }

  public JwfTask mkTaskNode(ASTTask task) {
    var node = new JwfTask(task.getName());
    flowElements.put(task.getName(), node);
    return node;
  }
}
