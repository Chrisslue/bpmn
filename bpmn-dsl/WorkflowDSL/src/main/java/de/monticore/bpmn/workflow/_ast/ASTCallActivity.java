package de.monticore.bpmn.workflow._ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTCallActivity extends ASTCallActivityTOP {

    protected ASTCallActivity() {
        super();
    }

    protected ASTCallActivity(
            final String template,
            final Optional<ASTCompensationHandler> compensationHandler,
            final Optional<ASTLoopCharacteristics> loopCharacteristics,
            final Optional<String> parentRef,
            final Optional<String> laneRef,
            final List<SequenceFlow> incomings,
            final List<SequenceFlow> outgoings,
            final String name,
            final Optional<ASTIOSpecification> iOSpecification,
            final List<ASTNamedEvent> boundaryEvents
    ) {
        super(
                template,
                compensationHandler,
                loopCharacteristics,
                parentRef,
                laneRef,
                incomings,
                outgoings,
                name,
                iOSpecification,
                boundaryEvents
        );
    }

    @Override
    public Collection<? extends ASTEvent> getBoundaryEvents() {
        return getBoundaryEventList();
    }

}
