package de.monticore.workflow.conformance.datastructure.analysis;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.interf.NodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.datastructure.interf.NodeType;
import de.monticore.workflow.conformance.datastructure.interf.WfBuilder;
import de.monticore.workflow.conformance.utils.WfNodeCollector;
import java.util.HashMap;
import java.util.Map;

/***
 * construct a workflow graph form the BPMN AST.
 */
public class WfGraphGenerator {
  Map<String,  NodeBuilder<WfNode>> flowElements = new HashMap<>();
  static  int counter = 0 ;
  WfBuilder<WfNode> builder  ;//todo declare

  public NodeBuilder<ConfWfNode> buildBpmn(ASTWorkflowCompilationUnit ast) {


    // traverse the Workflow ast a collect elements
    WfNodeCollector collector = new WfNodeCollector();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    // in the first pass first create vertices  and add them to the graph
    collector.getTasks().forEach(t -> flowElements.put(t.getName(), builder.mkNamedTask(t.getName())));
    collector.getNamedEvents().forEach(e -> flowElements.put(e.getName(), builder.mkNamedEvent(e.getName())));
    collector.getGateways().forEach(g -> flowElements.put(g.getName(),builder.mkNamedGateway(g.getName(),NodeType.OR_SPLIT))); //todo fix type

    for (ASTSequenceFlow sequenceFlow : collector.getSequenceFlows()) {
      for (int i = 0; i < sequenceFlow.getPathList().size()-1; i++) {
        NodeBuilder<WfNode> src = flowElements.get(sequenceFlow.getPathList().get(i).getNodeRef().getBaseName());
        NodeBuilder<WfNode> target = flowElements.get(sequenceFlow.getPathList().get(i+1).getNodeRef().getBaseName());

        src.addSuccessor(target);
      }
    }

    return  null; //todo return the start node
  }




}
