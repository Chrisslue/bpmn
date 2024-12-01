package de.monticore.bpmn.conformance.datastructures.interf;

import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.bpmn.workflow._ast.ASTTask;
import java.util.Set;

public interface WfBuilder { // todo find solution with parameter

  /****
   * Transform a task to a Workflow node. the tasks are uniquely identifying bei their names.
   * @param task the label of the task.
   */
  void mkNamedTask(ASTTask task);

  /****
   * Transform an event to a Workflow node. the events are uniquely identify by their names.
   * @param event the label of the event.
   */
  void mkNamedEvent(ASTNamedEvent event);

  /****
   * Transform a gateway to a Workflow node.
   * @param gateway the label of the event.
   */
  void mkNamedGateway(ASTNamedGateway gateway);

  /***
   * Transform the start event of the workflow  to a Workflow Node.
   * @param label the label of the start  event.
   */
  void mkStartEvent(ASTNamedEvent label);

  void mkEndEvent(ASTNamedEvent label);

  void mkSequence(ASTSequenceFlow sequenceFlow);

  void build();

  WfNode getWfNode(String name);

  Set<WfNode> getAllNodes();
}
