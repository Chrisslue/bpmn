package de.monticore.bpmn.collectors;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Filter a single AST node by its type.
 *
 * @param <E> the type of the filtered node
 */
public class WorkflowFilter<E extends ASTWorkflowNode> implements WorkflowInheritanceVisitor {

    private ASTWorkflowNode unfiltered;

    private E filtered;

    public WorkflowFilter(final ASTWorkflowNode node) {
        unfiltered = node;

        node.accept(getRealThis());
    }

    /**
     * Returns the filtered node.
     *
     * @return Optional containing the filtered node if selected or else an empty Optional
     */
    public Optional<E> getFiltered() {
        return Optional.ofNullable(filtered);
    }

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
