/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.collectors;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import de.monticore.bpmn.visitors.WorkflowLocalVisitor;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowNode;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import java.util.List;

/**
 * Traverses the AST and collects nodes of interest.
 *
 * <p>This collector DOES NOT TRAVERSE contained sub-processes.
 *
 * <p>Nodes are selected by calling {@code WorkflowLocalCollector#select(E)} in the {@code
 * WorkflowLocalCollector#visit(E)} method.
 *
 * @see WorkflowCollector
 * @param <E> the type of node
 */
public abstract class WorkflowLocalCollector<E> extends WorkflowLocalVisitor {
  
  protected List<E> result;
  
  public WorkflowLocalCollector(final ASTWorkflowNode root) {
    super(root);
  }
  
  /**
   * Collects and returns the selected nodes.
   *
   * @return the selected nodes
   * @see WorkflowLocalCollector#select(ASTWorkflowNode)
   */
  public List<E> collect(WorkflowLocalCollector<E> collector) {
    result = Lists.newArrayList();
    
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(collector);
    localRoot.accept(traverser);
    return collector.result;
  }
  
  /**
   * Selects a node by adding it to the result list.
   *
   * <p>This method should be called in {@code WorkflowLocalCollector#visit(E)} to select a node.
   *
   * @see WorkflowLocalCollector#visit(E)
   * @param node the node to add to the result list
   */
  protected void select(final E node) {
    checkNotNull(node);
    
    if (!node.equals(localRoot)) {
      result.add(node);
    }
  }
  
}
