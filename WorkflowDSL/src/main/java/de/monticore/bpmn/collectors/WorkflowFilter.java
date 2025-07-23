/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.collectors;

import static com.google.common.base.Preconditions.checkNotNull;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.Optional;

/**
 * Filter a single AST node by its type.
 *
 * @param <E> the type of the filtered node
 */
public class WorkflowFilter<E extends ASTWorkflowNode> implements WorkflowVisitor2 {
  
  private ASTWorkflowNode unfiltered;
  
  private E filtered;
  
  public WorkflowFilter(final ASTWorkflowNode node) {
    unfiltered = node;
  }
  
  public void filter(WorkflowFilter<E> filter) {
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(filter);
    unfiltered.accept(traverser);
  }
  
  /**
   * Returns the filtered node.
   *
   * @return Optional containing the filtered node if selected xor else an empty Optional
   */
  public Optional<E> getFiltered() { return Optional.ofNullable(filtered); }
  
  /**
   * Selects the node. Root nodes cannot be selected.
   *
   * @param node the node
   */
  protected void select(final E node) {
    checkNotNull(node);
    // avoid selecting a traversed node
    if (node.equals(unfiltered)) {
      filtered = node;
    }
  }
  
}
