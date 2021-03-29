/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.cocos;

import de.monticore.bpmn.cocos.activities.*;
import de.monticore.bpmn.cocos.events.*;
import de.monticore.bpmn.cocos.events.triggers.*;
import de.monticore.bpmn.cocos.flow.*;
import de.monticore.bpmn.cocos.gateways.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.monticore.expressions.timeexpressions.cocos.TemporalExpressionsCoCos;
import de.monticore.bpmn.cocos.analysis.*;
import de.monticore.bpmn.cocos.conditions.ExpressionHasCorrectTypes;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import ocl.monticoreocl.ocl._cocos.OCLCoCos;

/**
 * Factory for CoCo checkers.
 */
public class WorkflowCoCos {

    private WorkflowCoCos() {
    }

    /**
     * Returns the full CoCo checker.
     *
     * Checks basic CoCos, then structural CoCos, then behavioral CoCos
     *
     * @return the CoCo checker
     */
    public static WorkflowCoCoChecker getFullChecker() {
        final WorkflowCoCoChecker checker = getBasicChecker();
        checker.addChecker(getStructuralChecker());
        checker.addChecker(getBehavioralChecker());

        return checker;
    }

    /**
     * Returns the basic CoCo checker
     *
     * Checks basic (syntax-based) CoCos
     *
     * @return the CoCo checker
     */
    public static WorkflowCoCoChecker getBasicChecker() {
        final WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
        checker.addChecker(getActivityChecker());
        checker.addChecker(getGatewayChecker());
        checker.addChecker(getEventChecker()); // includes the event trigger checker
        checker.addChecker(getSequenceFlowChecker());
        checker.addChecker(getTypesChecker());
        checker.addChecker(getTimeExpressionsChecker());

        return checker;
    }

    /**
     * Returns the structural CoCo checker
     *
     * @return the CoCo checker
     */
    public static WorkflowCoCoChecker getStructuralChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo(new ProcessHasNoDisconnectedComponents())
                .addCoCo(new ProcessHasNoDeadNodes())
                .addCoCo(new ProcessHasNoInfiniteLoop())
                .addCoCo(new ProcessHasNoSyncDeadlock())
                .addCoCo(new ProcessHasNoLackOfSync());
    }

    /**
     * Returns the behavioral CoCo checker
     *
     * @return the CoCo checker
     */
    public static WorkflowCoCoChecker getBehavioralChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo(new ProcessNetIsSound());
    }

    public static WorkflowCoCoChecker getSequenceFlowChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo(new SequenceFlowDoesNotCrossSubProcessBoundaries())
                .addCoCo(new SequenceFlowNodeReferencesExist())
                .addCoCo(new BoundaryEventHasNoIncomingFlow())
                .addCoCo(new EndEventHasNoOutgoingFlow())
                .addCoCo(new EndEventHasOneOrMoreIncomingFlows())
                .addCoCo(new IntermediateEventHasOneOrMoreIncomingFlows())
                .addCoCo(new IntermediateEventHasOneOrMoreOutgoingFlows())
                .addCoCo(new StartEventHasNoIncomingFlow())
                .addCoCo(new StartEventHasOneOrMoreOutgoingFlows())
                .addCoCo(new SplitGatewayHasAtMostOneIncomingFlow())
                .addCoCo(new SplitGatewayHasMultipleOutgoingFlow())
                .addCoCo(new MergeGatewayHasAtMostOneOutgoingFlow())
                .addCoCo(new MergeGatewayHasMultipleIncomingFlow());
    }

    public static WorkflowCoCoChecker getGatewayChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo(new EventGatewayDoesNotMixMessageEventsAndReceiveTasks())
                .addCoCo(new EventGatewayHasTwoOrMoreOutgoingFlows())
                .addCoCo(new EventGatewayIsSplit())
                .addCoCo(new EventGatewayOutgoingFlowHasNoCondition())
                .addCoCo(new EventGatewayHasValidTarget())
                .addCoCo(new EventGatewayTargetHasNoAdditionalIncomingFlow())
                .addCoCo(new EventGatewayTargetReceiveTaskHasNoBoundaryEvents())
                .addCoCo(new ParallelEventGatewayHasNoIncomingFlow());
    }

    public static WorkflowCoCoChecker getActivityChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo(new TaskContainsOnlyBoundaryEvents())
                .addCoCo(new CompensationActivityHasNoIncomingOrOutgoingFlow())
                .addCoCo(new EventSubProcessHasNoIncomingOrOutgoingFlow())
                .addCoCo(new EventSubProcessHasOnlyOneStartEvent())
                .addCoCo(new AdHocSubProcessContainsAtLeastOneActivity())
                .addCoCo(new AdHocSubProcessHasNoStartAndEndEvent())
                .addCoCo(new LoopCountExpressionReturnsIntegerNumber());
    }

    public static WorkflowCoCoChecker getEventChecker() {
        final WorkflowCoCoChecker checker = new WorkflowCoCoChecker()
                .addCoCo(new AtLeastOneEndEventIfStartEventIsUsed())
                .addCoCo(new AtLeastOneStartEventIfEndEventIsUsed())
                .addCoCo(new BoundaryEventIsContainedByActivity())
                .addCoCo(new BoundaryEventIsNotThrowing())
                .addCoCo(new CompensatedActivityExists())
                .addCoCo(new EndEventIsNotCatching())
                .addCoCo(new IntermediateEventIsEitherThrowOrCatch())
                .addCoCo(new StartEventIsNotThrowing())
                .addCoCo(new StartEventOutgoingFlowHasNoCondition());
        checker.addChecker(getEventTriggerChecker());

        return checker;
    }

    public static WorkflowCoCoChecker getEventTriggerChecker() {
        return new WorkflowCoCoChecker()
                .addCoCo((WorkflowASTProcessCoCo) new CancelIntermediateEventIsAttachedToTransaction())
                .addCoCo((WorkflowASTSubProcessCoCo) new CancelIntermediateEventIsAttachedToTransaction())
                .addCoCo((WorkflowASTProcessCoCo) new CancelEndEventIsContainedWithinTransaction())
                .addCoCo((WorkflowASTSubProcessCoCo) new CancelEndEventIsContainedWithinTransaction())
                .addCoCo(new StartEventTopLevelProcessHasValidTrigger())
                .addCoCo(new StartEventSubProcessHasValidTrigger())
                .addCoCo(new IntermediateCatchEventHasValidTrigger())
                .addCoCo(new IntermediateThrowEventHasValidTrigger())
                .addCoCo(new BoundaryEventHasValidTrigger())
                .addCoCo(new EndEventHasValidTrigger())
                .addCoCo(new NonInterruptingEventHasValidTrigger())
                .addCoCo((WorkflowASTProcessCoCo) new NonInterruptingEventIsSubProcessStartOrBoundary())
                .addCoCo((WorkflowASTSubProcessCoCo) new NonInterruptingEventIsSubProcessStartOrBoundary())
                .addCoCo(new CompensateCatchEventIsNotPartOfNormalFlow());
    }

    public static WorkflowCoCoChecker getTimeExpressionsChecker() {
        final WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
        checker.addChecker(TemporalExpressionsCoCos.createChecker());

        return checker;
    }

    public static WorkflowCoCoChecker getTypesChecker() {
        final WorkflowCoCoChecker checker = new WorkflowCoCoChecker()
                .addCoCo(new ExpressionHasCorrectTypes())
                .addCoCo(new CalledElementDoesExist());
        checker.addChecker(OCLCoCos.createChecker());

        return checker;
    }

}
