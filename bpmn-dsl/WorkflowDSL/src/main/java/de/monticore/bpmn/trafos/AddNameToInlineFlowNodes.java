package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTInlineGateway;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

/**
 * Adds anonymous names to in-lined gateways and events.
 */
public class AddNameToInlineFlowNodes extends WorkflowTransformation implements WorkflowVisitor {

    private int nextAnonymousId = 1;

    private Integer getNextId() {
        return nextAnonymousId++;
    }

    @Override
    protected void transform() {
        getAst().accept(this);
    }

    @Override
    public void visit(final ASTInlineGateway gateway) {
        gateway.setName("_Gateway_" + getNextId());
    }

    @Override
    public void visit(final ASTInlineEvent event) {
        event.setName("_Event_" + getNextId());
    }

}
