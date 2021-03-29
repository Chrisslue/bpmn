package de.monticore.bpmn.workflow._ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTTask extends ASTTaskTOP {

    protected  ASTTask() {
        super();
    }

    protected  ASTTask(
            final Optional<ASTTaskType> type,
            final Optional<ASTTaskTypeAttributes> taskTypeAttributes,
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
                type,
                taskTypeAttributes,
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
