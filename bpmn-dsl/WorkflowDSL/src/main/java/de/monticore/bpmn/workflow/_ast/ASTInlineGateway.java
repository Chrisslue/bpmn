package de.monticore.bpmn.workflow._ast;

import java.util.List;
import java.util.Optional;

public class ASTInlineGateway extends ASTInlineGatewayTOP {

    private String name;

    protected ASTInlineGateway() {
        super();
    }

    protected ASTInlineGateway(
            final Optional<ASTCondition> activationCondition,
            final Optional<String> parentRef,
            final Optional<String> laneRef,
            final List<SequenceFlow> incomings,
            final List<SequenceFlow> outgoings,
            final ASTGatewayDirection direction,
            final ASTGatewayType type
    ) {
        super(activationCondition, parentRef, laneRef, incomings, outgoings, direction, type);
    }

    public boolean isDiverging() {
        return sizeOutgoings() > 1;
    }

    public boolean isConverging() {
        return sizeIncomings() > 1;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
