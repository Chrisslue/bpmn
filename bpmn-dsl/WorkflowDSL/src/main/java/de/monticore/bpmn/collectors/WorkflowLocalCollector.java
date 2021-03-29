package de.monticore.bpmn.collectors;

import com.google.common.collect.Lists;
import de.monticore.bpmn.visitors.WorkflowLocalInheritanceVisitor;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTWorkflowNode;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Traverses the AST and collects nodes of interest.
 *
 * This collector DOES NOT TRAVERSE contained sub-processes.
 *
 * Nodes are selected by calling {@code WorkflowLocalCollector#select(E)} in the {@code WorkflowLocalCollector#visit(E)} method.
 *
 * @see WorkflowCollector
 *
 * @param <E> the type of node
 */
public abstract class WorkflowLocalCollector<E extends ASTWorkflowNode> extends WorkflowLocalInheritanceVisitor {

    protected List<E> result;

    public WorkflowLocalCollector(final ASTFlowElementContainer root) {
        super(root);
    }

    /**
     * Collects and returns the selected nodes.
     *
     * @return the selected nodes
     *
     * @see WorkflowLocalCollector#select(ASTWorkflowNode)
     */
    public List<E> collect() {
        result = Lists.newArrayList();

        localRoot.accept(getRealThis());
        return result;
    }

    /**
     * Selects a node by adding it to the result list.
     *
     * This method should be called in {@code WorkflowLocalCollector#visit(E)} to select a node.
     *
     * @see WorkflowLocalCollector#visit(E)
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