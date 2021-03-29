package de.monticore.bpmn.workflow._ast;

import java.util.List;
import java.util.Optional;

public class ASTInlineEvent extends ASTInlineEventTOP {

    private String name;

    protected ASTInlineEvent() {
        super();
    }

    protected ASTInlineEvent(
            final Optional<String> parentRef,
            final Optional<String> laneRef,
            final List<SequenceFlow> incomings,
            final List<SequenceFlow> outgoings,
            final Optional<ASTEventType> type,
            final Optional<ASTEventBehavior> behavior,
            final Optional<ASTEventTrigger> trigger
    ) {
        super(parentRef, laneRef, incomings, outgoings, type, behavior, trigger);
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
