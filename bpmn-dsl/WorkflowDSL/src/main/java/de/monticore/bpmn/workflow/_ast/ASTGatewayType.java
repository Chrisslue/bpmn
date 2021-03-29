package de.monticore.bpmn.workflow._ast;

public class ASTGatewayType extends ASTGatewayTypeTOP {

    protected ASTGatewayType() {
        super();
    }

    protected ASTGatewayType(
            final boolean exclusive,
            final boolean inclusive,
            final boolean parallel,
            final boolean exclusiveEventBased,
            final boolean parallelEventBased,
            final boolean complex
    ) {
        super(exclusive, inclusive, parallel, exclusiveEventBased, parallelEventBased, complex);
    }

    public boolean isEventBased() {
        return isExclusiveEventBased() || isParallelEventBased();
    }

}
