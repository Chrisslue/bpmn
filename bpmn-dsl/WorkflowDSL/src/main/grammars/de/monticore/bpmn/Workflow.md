
# Business Process Model and Notation (BPMN)
The main purpose of this language is to provide a textual alternative to graphical **BPMN** modeling.

This BPMN language component contains 
* one grammar,
* context conditions,
* pretty printers, and 
* a command-line tool.

## An Example Model

```
package de.monticore.bpmn.examples.vacation;

import de.monticore.bpmn.cds.Vacation.*;

process RequestVacation {

  lane Admin {
    store contract:Contract;
    data report:Report;
    data notice:VacationCardEntry;

    task service DoWork {
      out: report;
    }
    task user Task1 {
      io: {contract, report} -> {report};
    }
    task user Task2 {
      io: {report} -> {notice};
    }
    task Foo; // template:External;
    task service Bar;

    event start -> DoWork -> Task1 -> split xor -> {
      [true] Foo,
      [false] Bar
    } -> merge xor -> event receive timer:[after PT20S] -> Task2 -> event end;
  }

}
```

The following example represents a simplified vacation request process:
* This example defines a Process named `RequestVacation`.
* There is a single lane called `admin`.
* Three data objects are used in this process: `Contract`,`Report` and `VacationCardEntry`.
* Five tasks are specified in this example: `DoWork`, `Task1`, `Task2`, `Foo` and `Bar`.






Further examples can be found here.


## Context Conditions (CoCos)

This sections lists the context conditions for the BPMN language.


Context conditions for activities:

* [```AdHocSubProcessContainsAtLeastOneActivity```](../../../../java/de/monticore/bpmn/cocos/activities/AdHocSubProcessContainsAtLeastOneActivity.java)  

* [```AdHocSubProcessHasNoStartAndEndEvent```](../../../../java/de/monticore/bpmn/cocos/activities/AdHocSubProcessHasNoStartAndEndEvent.java)  

* [```CalledElementDoesExist```](../../../../java/de/monticore/bpmn/cocos/activities/CalledElementDoesExist.java) 

* [```CompensationActivityHasNoIncomingOrOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/activities/CompensationActivityHasNoIncomingOrOutgoingFlow.java)  

* [```EventSubProcessHasNoIncomingOrOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/activities/EventSubProcessHasNoIncomingOrOutgoingFlow.java)  

* [```EventSubProcessHasOnlyOneStartEvent```](../../../../java/de/monticore/bpmn/cocos/activities/EventSubProcessHasOnlyOneStartEvent.java)  

* [```LoopCountExpressionReturnsIntegerNumber```](../../../../java/de/monticore/bpmn/cocos/activities/LoopCountExpressionReturnsIntegerNumber.java)  

* [```TaskContainsOnlyBoundaryEvents```](../../../../java/de/monticore/bpmn/cocos/activities/TaskContainsOnlyBoundaryEvents.java)  



Context conditions for analysis:

* [```ProcessHasNoDeadNodes```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessHasNoDeadNodes.java) 

* [```ProcessHasNoDisconnectedComponents```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessHasNoDisconnectedComponents.java) 

* [```ProcessHasNoInfiniteLoop```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessHasNoInfiniteLoop.java) 

* [```ProcessHasNoLackOfSync```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessHasNoLackOfSync.java) 

* [```ProcessHasNoSyncDeadlock```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessHasNoSyncDeadlock.java) 

* [```ProcessNetIsSound```](../../../../java/de/monticore/bpmn/cocos/analysis/ProcessNetIsSound.java) 


Context conditions for event triggers:

* [```BoundaryEventHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/BoundaryEventHasValidTrigger.java) 

* [```CancelEndEventIsContainedWithinTransaction```](../../../../java/de/monticore/bpmn/cocos/event/triggers/CancelEndEventIsContainedWithinTransaction.java) 

* [```CancelIntermediateEventIsAttachedToTransaction```](../../../../java/de/monticore/bpmn/cocos/event/triggers/CancelIntermediateEventIsAttachedToTransaction.java) 

* [```CompensateCatchEventIsNotPartOfNormalFlow```](../../../../java/de/monticore/bpmn/cocos/event/triggers/CompensateCatchEventIsNotPartOfNormalFlow.java) 

* [```EndEventHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/EndEventHasValidTrigger.java) 

* [```IntermediateCatchEventHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/IntermediateCatchEventHasValidTrigger.java) 

* [```NonInterruptingEventHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/NonInterruptingEventHasValidTrigger.java) 

* [```StartEventSubProcessHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/StartEventSubProcessHasValidTrigger.java) 

* [```StartEventTopLevelProcessHasValidTrigger```](../../../../java/de/monticore/bpmn/cocos/event/triggers/StartEventTopLevelProcessHasValidTrigger.java) 


Context conditions for events:

* [```AtLeastOneEndEventIfStartEventIsUsed```](../../../../java/de/monticore/bpmn/cocos/event/triggers/AtLeastOneEndEventIfStartEventIsUsed.java) 

* [```AtLeastOneStartEventIfEndEventIsUsed```](../../../../java/de/monticore/bpmn/cocos/event/triggers/AtLeastOneStartEventIfEndEventIsUsed.java)

* [```BoundaryEventIsContainedByActivity```](../../../../java/de/monticore/bpmn/cocos/event/triggers/BoundaryEventIsContainedByActivity.java)

* [```BoundaryEventIsNotThrowing```](../../../../java/de/monticore/bpmn/cocos/event/triggers/BoundaryEventIsNotThrowing.java)

* [```CompensatedActivityExists```](../../../../java/de/monticore/bpmn/cocos/event/triggers/CompensatedActivityExists.java)

* [```EndEventIsNotCatching```](../../../../java/de/monticore/bpmn/cocos/event/triggers/EndEventIsNotCatching.java)

* [```IntermediateEventIsEitherThrowOrCatch```](../../../../java/de/monticore/bpmn/cocos/event/triggers/IntermediateEventIsEitherThrowOrCatch.java)

* [```NonInterruptingEventIsSubProcessStartOrBoundary```](../../../../java/de/monticore/bpmn/cocos/event/triggers/NonInterruptingEventIsSubProcessStartOrBoundary.java)

* [```StartEventIsNotThrowing```](../../../../java/de/monticore/bpmn/cocos/event/triggers/StartEventIsNotThrowing.java)

* [```StartEventOutgoingFlowHasNoCondition```](../../../../java/de/monticore/bpmn/cocos/event/triggers/StartEventOutgoingFlowHasNoCondition.java)


Context conditions for flow:

* [```AtMostOneDefaultBranch```](../../../../java/de/monticore/bpmn/cocos/flow/AtMostOneDefaultBranch.java)

* [```AtMostOneOutgoingFlowIsDefault```](../../../../java/de/monticore/bpmn/cocos/flow/AtMostOneOutgoingFlowIsDefault.java)
  is not implemented.

* [```BoundaryEventHasNoIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/BoundaryEventHasNoIncomingFlow.java)

* [```DefaultBranchIsLastBranch```](../../../../java/de/monticore/bpmn/cocos/flow/DefaultBranchIsLastBranch.java)

* [```DefaultFlowHasValidSource```](../../../../java/de/monticore/bpmn/cocos/flow/DefaultFlowHasValidSource.java)
  is not implemented.

* [```EndEventHasNoOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/EndEventHasNoOutgoingFlow.java)

* [```EndEventHasOneOrMoreIncomingFlows```](../../../../java/de/monticore/bpmn/cocos/flow/EndEventHasOneOrMoreIncomingFlows.java)

* [```IntermediateEventHasOneOrMoreIncomingFlows```](../../../../java/de/monticore/bpmn/cocos/flow/IntermediateEventHasOneOrMoreIncomingFlows.java)

* [```IntermediateEventHasOneOrMoreOutgoingFlows```](../../../../java/de/monticore/bpmn/cocos/flow/IntermediateEventHasOneOrMoreOutgoingFlows.java)

* [```MergeGatewayHasAtMostOneOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/MergeGatewayHasAtMostOneOutgoingFlow.java)

* [```MergeGatewayHasMultipleIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/MergeGatewayHasMultipleIncomingFlow.java)

* [```SequenceFlowDoesNotCrossSubProcessBoundaries```](../../../../java/de/monticore/bpmn/cocos/flow/SequenceFlowDoesNotCrossSubProcessBoundaries.java)

* [```SequenceFlowNodeReferencesExist```](../../../../java/de/monticore/bpmn/cocos/flow/SequenceFlowNodeReferencesExist.java)

* [```SplitGatewayHasAtMostOneIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/SplitGatewayHasAtMostOneIncomingFlow.java)

* [```SplitGatewayHasMultipleOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/SplitGatewayHasMultipleOutgoingFlow.java)

* [```StartEventHasNoIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/StartEventHasNoIncomingFlow.java)

* [```StartEventHasOneOrMoreOutgoingFlows```](../../../../java/de/monticore/bpmn/cocos/flow/StartEventHasOneOrMoreOutgoingFlows.java)


Context conditions for gateways:

* [```EventGatewayDoesNotMixMessageEventsAndReceiveTasks```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayDoesNotMixMessageEventsAndReceiveTasks.java)

* [```EventGatewayHasTwoOrMoreOutgoingFlows```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayHasTwoOrMoreOutgoingFlows.java)

* [```EventGatewayHasValidTarget```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayHasValidTarget.java)

* [```EventGatewayIsSplit```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayIsSplit.java)

* [```EventGatewayOutgoingFlowHasNoCondition```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayOutgoingFlowHasNoCondition.java)

* [```EventGatewayTargetHasNoAdditionalIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayTargetHasNoAdditionalIncomingFlow.java)

* [```EventGatewayTargetReceiveTaskHasNoBoundaryEvents```](../../../../java/de/monticore/bpmn/cocos/gateways/EventGatewayTargetReceiveTaskHasNoBoundaryEvents.java)

* [```TaskContainsOnlyBoundaryEvents```](../../../../java/de/monticore/bpmn/cocos/gateways/TaskContainsOnlyBoundaryEvents.java)


