package de.monticore.wf2ltl.collector;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

import java.util.List;

public class EndEventCollector extends EventCollector {

  public EndEventCollector(WorkflowTraverser traverser) {
    super(traverser);
  }

  public static List<ASTEvent> of(List<ASTFlowElement> flowElements) {
    var traverser = WorkflowMill.traverser();
    var collector = new EndEventCollector(traverser);
    for (ASTFlowElement flowElement : flowElements) {
      flowElement.accept(traverser);
    }
    return collector.getEvents();
  }

  @Override
  public void handle(ASTNamedEvent namedEvent) {
    if (namedEvent.isEnd()) {
      super.getEvents().add(namedEvent);
    }
  }

  @Override
  public void handle(ASTInlineEvent node) {
    if (node.isEnd()) {
      super.getEvents().add(node);
    }
  }

}
