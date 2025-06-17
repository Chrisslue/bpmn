 /* (c) https://github.com/MontiCore/monticore */ 
/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.cocos;

import de.monticore.bpmn.cocos.activities.*;
import de.monticore.bpmn.cocos.analysis.*;
import de.monticore.bpmn.cocos.events.*;
import de.monticore.bpmn.cocos.events.triggers.*;
import de.monticore.bpmn.cocos.expressions.*;
import de.monticore.bpmn.cocos.flow.*;
import de.monticore.bpmn.cocos.gateways.*;
import de.monticore.bpmn.types3.WorkflowTypeCheck3;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.timerconditions.cocos.TemporalExpressionsCoCos;

/** Factory for CoCo checkers. */
public class WorkflowCoCos {

  private WorkflowCoCos() {}

  /**
   * Returns the full CoCo checker.
   *
   * <p>Checks basic CoCos, then structural CoCos, then behavioral CoCos
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
   * <p>Checks basic (syntax-based) CoCos
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
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    //checker.addCoCo(new ProcessHasNoDisconnectedComponents());
    checker.addCoCo(new ProcessHasNoDeadNodes());
    checker.addCoCo(new ProcessHasNoInfiniteLoop());
    //checker.addCoCo(new ProcessHasNoSyncDeadlock());
    //checker.addCoCo(new ProcessHasNoLackOfSync());
    return checker;
  }

  /**
   * Returns the behavioral CoCo checker
   *
   * @return the CoCo checker
   */
  public static WorkflowCoCoChecker getBehavioralChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    // checker.addCoCo(new ProcessNetIsSound());
    return checker;
  }

  public static WorkflowCoCoChecker getSequenceFlowChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new SequenceFlowDoesNotCrossSubProcessBoundaries());
    checker.addCoCo(new SequenceFlowNodeReferencesExist());
    checker.addCoCo(new BoundaryEventHasNoIncomingFlow());
    checker.addCoCo(new EndEventHasNoOutgoingFlow());
    checker.addCoCo(new EndEventHasOneOrMoreIncomingFlows());
    checker.addCoCo(new IntermediateEventHasOneOrMoreIncomingFlows());
    checker.addCoCo(new IntermediateEventHasOneOrMoreOutgoingFlows());
    checker.addCoCo(new StartEventHasNoIncomingFlow());
    checker.addCoCo(new StartEventHasOneOrMoreOutgoingFlows());
    checker.addCoCo(new SplitGatewayHasAtMostOneIncomingFlow());
    checker.addCoCo(new SplitGatewayHasMultipleOutgoingFlow());
    checker.addCoCo(new MergeGatewayHasAtMostOneOutgoingFlow());
    checker.addCoCo(new MergeGatewayHasMultipleIncomingFlow());
    checker.addCoCo(new FlowBlockMinTwo());
    return checker;
  }

  public static WorkflowCoCoChecker getGatewayChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new EventGatewayDoesNotMixMessageEventsAndReceiveTasks());
    checker.addCoCo(new EventGatewayHasTwoOrMoreOutgoingFlows());
    checker.addCoCo(new EventGatewayIsSplit());
    checker.addCoCo(new EventGatewayOutgoingFlowHasNoCondition());
    checker.addCoCo(new EventGatewayHasValidTarget());
    checker.addCoCo(new EventGatewayTargetHasNoAdditionalIncomingFlow());
    checker.addCoCo(new EventGatewayTargetReceiveTaskHasNoBoundaryEvents());
    checker.addCoCo(new ParallelEventGatewayHasNoIncomingFlow());
    return checker;
  }

  public static WorkflowCoCoChecker getActivityChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new CompensationActivityHasNoIncomingOrOutgoingFlow());
    checker.addCoCo(new EventSubProcessHasOnlyOneStartEvent());
    checker.addCoCo(new AdHocSubProcessContainsAtLeastOneActivity());
    checker.addCoCo(new AdHocSubProcessHasNoStartAndEndEvent());
    checker.addCoCo(new LoopCountExpressionReturnsIntegerNumber());
    checker.addCoCo(new TaskTypeAttributesAreSet());
    checker.addCoCo(new AdHocSubProcessHasAdHocCharacteristics());
    return checker;
  }

  public static WorkflowCoCoChecker getEventChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new AtLeastOneEndEventIfStartEventIsUsed());
    checker.addCoCo(new AtLeastOneStartEventIfEndEventIsUsed());
    checker.addCoCo(new BoundaryEventIsContainedByActivity());
    checker.addCoCo(new BoundaryEventIsNotThrowing());
    checker.addCoCo(new CompensatedActivityExists());
    checker.addCoCo(new EndEventIsNotCatching());
    checker.addCoCo(new IntermediateEventIsEitherThrowOrCatch());
    checker.addCoCo(new StartEventIsNotThrowing());
    checker.addCoCo(new StartEventOutgoingFlowHasNoCondition());
    checker.addChecker(getEventTriggerChecker());
    return checker;
  }

  public static WorkflowCoCoChecker getEventTriggerChecker() {
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(
        (WorkflowASTWFSubProcessCoCo) new CancelIntermediateEventIsAttachedToTransaction());
    checker.addCoCo((WorkflowASTWFSubProcessCoCo) new CancelEndEventIsContainedWithinTransaction());
    checker.addCoCo(new StartEventTopLevelProcessHasValidTrigger());
    checker.addCoCo(new StartEventSubProcessHasValidTrigger());
    checker.addCoCo(new IntermediateCatchEventHasValidTrigger());
    checker.addCoCo(new IntermediateThrowEventHasValidTrigger());
    checker.addCoCo(new BoundaryEventHasValidTrigger());
    checker.addCoCo(new EndEventHasValidTrigger());
    checker.addCoCo(new NonInterruptingEventHasValidTrigger());
    checker.addCoCo(
        (WorkflowASTWFSubProcessCoCo) new NonInterruptingEventIsSubProcessStartOrBoundary());
    checker.addCoCo(new CompensateCatchEventIsNotPartOfNormalFlow());
    return checker;
  }

  public static WorkflowCoCoChecker getTimeExpressionsChecker() {
    final WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addChecker(TemporalExpressionsCoCos.createChecker());

    return checker;
  }

  // currently not working
  
  public static WorkflowCoCoChecker getTypesChecker() {
    // assure that OCL TypeCheck is used
    // As of writing, this is valid, as long as only OCL expressions are used
    // should other expressions be included in this language,
    // another TypeChecker will be required to be initialized.
    WorkflowTypeCheck3.init();
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new CompletionConditionIsBoolean());
    checker.addCoCo(new StandardLoopConditionIsBoolean());
    checker.addCoCo(new MILoopCompletionConditionIsBoolean());
    checker.addCoCo(new ComplexConditionOfImplicitEventBehaviorIsBoolean());
    checker.addCoCo(new LoopCardinalityExpressionIsBoolean());
    checker.addCoCo(new GuardOfComplexGatewayIsBoolean());
    checker.addCoCo(new ExpressionOfEventTriggerConditionalIsBoolean());
    checker.addCoCo(new FlowConditionIsBoolean());

    //checker.addCoCo(new CalledElementDoesExist());
    //checker.addChecker(OCLCoCos.createChecker());

    return checker;
  }
  
}
