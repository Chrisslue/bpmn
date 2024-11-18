package de.monticore.workflow.conformance.datastructure.interf;

import de.monticore.workflow.conformance.utils.NodeType;

public interface WfBuilder<Node> {

  /****
   * Transform a task to a Workflow node.
   * @param label the label of the task.
   */
  Node mkNamedTask(String label);

  /****
   * Transform an event to a Workflow node.
   * @param label the label of the event.
   */
  Node mkNamedEvent(String label);

  /****
   * Transform an gateway to a Workflow node.
   * @param label the label of the event.
   * @param type the type of the gateway.
   */
  Node mkNamedGateway(String label, NodeType type);

  /***
   * Transform the start event of the workflow  to a Workflow Node.
   * @param label the label of the start  event.
   */
  Node mkStartEvent(String label);


  Node getStartEvent();
}
