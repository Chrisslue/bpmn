package de.monticore.bpmn.wf2lts.collector;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import java.util.List;

public class StartEventCollector extends EventCollector {

  public StartEventCollector(WorkflowTraverser traverser) {
    super(traverser);
  }

  public static List<ASTEvent> of(List<ASTFlowElement> flowElements) {
    var traverser = WorkflowMill.traverser();
    var collector = new StartEventCollector(traverser);
    for (ASTFlowElement flowElement : flowElements) {
      flowElement.accept(traverser);
    }
    return collector.getEvents();
  }

  @Override
  public void handle(ASTNamedEvent namedEvent) {
    if (namedEvent.isStart()) {
      super.getEvents().add(namedEvent);
    }
  }

  @Override
  public void handle(ASTInlineEvent node) {
    if (node.isStart()) {
      super.getEvents().add(node);
    }
  }
}
