package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._util.WorkflowTypeDispatcher;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;
import java.util.HashMap;
import java.util.Map;

public class UniqueStartAndEndEventNaming implements NamingStrategy<ASTFlowNode>, WorkflowVisitor2 {

  private final String endName;
  private final String terminatingName;
  private final String startName;

  private final Map<ASTFlowNode, String> map;
  private final WorkflowTraverser traverser;

  public UniqueStartAndEndEventNaming(String startName, String endName, String terminatingName) {
    this.endName = endName;
    this.terminatingName = terminatingName;
    this.startName = startName;
    this.map = new HashMap<>();
    this.traverser = WorkflowMill.traverser();
    traverser.add4Workflow(this);
  }

  @Override
  public String apply(ASTFlowNode flowNode) {
    flowNode.accept(traverser);
    if (!map.containsKey(flowNode)) {
      // If flowNode is not a key it is not a start or end event.
      if (flowNode.getName().equals(startName)
          || flowNode.getName().equals(endName)
          || flowNode.getName().equals(terminatingName)) {
        Log.warn("Name of flowNode clashes with fixed event name: " + flowNode.getName());
      }
    }
    map.putIfAbsent(flowNode, flowNode.getName());
    return map.get(flowNode);
  }

  private void visitEvent(ASTEvent node) {
    if (node.isEnd()) {
      if (node.isPresentTrigger()
          && new WorkflowTypeDispatcher().isWorkflowASTEventTriggerTerminate(node.getTrigger())) {
        map.put(node, terminatingName);
      } else {
        map.put(node, endName);
      }
    } else {
      map.put(node, startName);
    }
  }

  @Override
  public void visit(ASTInlineEvent node) {
    visitEvent(node);
  }

  @Override
  public void visit(ASTNamedEvent node) {
    visitEvent(node);
  }

  public String getEndName() {
    return endName;
  }

  public String getTerminatingName() {
    return terminatingName;
  }

  public String getStartName() {
    return startName;
  }
}
