/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.datastructures.interf;

import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import java.util.Set;

/***
 * this interface to abstract form
 */
public interface WfBuilder {
  
  /****
   * Transform a task to a Workflow node. the tasks are uniquely identifying bei their names.
   *
   * @param task the label of the task.
   */
  void mkNamedTask(ASTWFTask task);
  
  /****
   * Transform an event to a Workflow node. the events are uniquely identified by their names.
   *
   * @param event the label of the event.
   */
  void mkNamedEvent(ASTWFEvent event);
  
  /****
   * Transform a gateway to a Workflow node.
   *
   * @param gateway the label of the event.
   */
  void mkNamedGateway(ASTWFGateway gateway);
  
  /***
   * Transform the start event of the workflow to a Workflow Node.
   *
   * @param label the label of the start event.
   */
  void mkStartEvent(ASTWFEvent label);
  
  void mkEndEvent(ASTWFEvent label);
  
  void mkSequence(ASTSequenceFlow sequenceFlow);
  
  void build();
  
  WfNode getWfNode(String name);
  
  Set<WfNode> getAllNodes();
  
}
