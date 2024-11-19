package de.monticore.workflow.conformance.datastructure.interf;

import de.monticore.workflow.conformance.utils.NodeType;

public interface WfBuilder<Node> {

  /****
   * Transform a task to a Workflow node. the tasks are uniquely identify bei their names.
   * @param label the label of the task.
   */
  void mkNamedTask(String label);

  /****
   * Transform an event to a Workflow node. the events are uniquely identify by their names.
   * @param label the label of the event.
   */
  void mkNamedEvent(String label);

  /****
   * Transform an gateway to a Workflow node.
   * @param label the label of the event.
   * @param type the type of the gateway.
   */
  void mkNamedGateway(String label, NodeType type);

  /***
   * Transform the start event of the workflow  to a Workflow Node.
   * @param label the label of the start  event.
   */
  void mkStartEvent(String label);
}
