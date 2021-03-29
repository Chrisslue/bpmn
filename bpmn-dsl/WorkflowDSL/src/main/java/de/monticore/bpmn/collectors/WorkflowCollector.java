package de.monticore.bpmn.collectors;

import com.google.common.collect.Lists;
import de.monticore.bpmn.workflow._ast.ASTLane;
import de.monticore.bpmn.workflow._ast.ASTWorkflowNode;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Traverses the AST and collects nodes of interest.
 *
 * Nodes are selected by calling {@code WorkflowCollector#select(E)} in the {@code WorkflowCollector#visit(E)} method.
 *
 * @see WorkflowLocalCollector
 *
 * @param <E> the type of node
 */
public abstract class WorkflowCollector<E extends ASTWorkflowNode> implements WorkflowInheritanceVisitor {

    private final ASTWorkflowNode localRoot;

    private List<E> result;

    public WorkflowCollector(final ASTWorkflowNode localRoot) {
        this.localRoot = localRoot;
    }

    /**
     * Collects and returns the selected nodes.
     *
     * @return the selected nodes
     *
     * @see WorkflowCollector#select(ASTWorkflowNode)
     */
    public List<E> collect() {
        result = Lists.newArrayList();

        localRoot.accept(getRealThis());
        return result;
    }

    /**
     * Selects a node by adding it to the result list.
     *
     * This method should be called in {@code WorkflowCollector#visit(E)} to select a node.
     *
     * @see WorkflowCollector#visit(E)
     *
     * @param node the node to add to the result list
     */
    protected void select(final E node) {
        checkNotNull(node);

        if (!node.equals(localRoot)) {
            result.add(node);
        }
    }

}
