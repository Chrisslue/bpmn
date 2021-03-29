package de.monticore.bpmn.visitors;

import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;

/**
 * An inheritance-aware visitor that does not traverse contained sub-processes.
 */
public abstract class WorkflowLocalInheritanceVisitor extends WorkflowLocalVisitor implements WorkflowInheritanceVisitor {

    public WorkflowLocalInheritanceVisitor(final ASTFlowElementContainer localRoot) {
        super(localRoot);
    }

}
