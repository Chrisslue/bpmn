package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.collectors.WorkflowLocalCollector;

import java.util.List;
import java.util.Optional;

public class ASTSubProcess extends ASTSubProcessTOP {

    protected ASTSubProcess() {
        super();
    }

    protected ASTSubProcess(
            boolean triggeredByEvent,
            ASTSubProcessType type,
            Optional<ASTAdHocCharacteristics> adHocCharacteristics,
            ASTIOSpecification iOSpecification,
            List<ASTLane> lanes,
            Optional<String> parentFlowNode,
            Optional<String> lane,
            List<SequenceFlow> incomings,
            List<SequenceFlow> outgoings,
            Optional<ASTCompensationHandler> compensationHandler,
            Optional<ASTLoopCharacteristics> loopCharacteristics,
            String name,
            List<ASTFlowElement> flowElements
    ) {
        super(
                triggeredByEvent,
                type,
                adHocCharacteristics,
                iOSpecification,
                lanes,
                parentFlowNode,
                lane,
                incomings,
                outgoings,
                compensationHandler,
                loopCharacteristics,
                name,
                flowElements
        );
    }

    public boolean isAdHoc() {
        return getType() == ASTSubProcessType.ADHOC;
    }

    public boolean isTransaction() {
        return getType() == ASTSubProcessType.TRANSACTION;
    }

    public List<ASTEvent> getBoundaryEvents() {
        return new WorkflowLocalCollector<ASTEvent>(this) {
            @Override
            public void visit(final ASTEvent event) {
                if (event.isBoundary()) {
                    select(event);
                }
            }
        }.collect();
    }

}
