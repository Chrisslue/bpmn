
# Business Process Model and Notation (BPMN)
The main purpose of this language is to provide a textual alternative to graphical **BPMN** modeling.

This BPMN language component contains 
* one grammar,
* context conditions,
* pretty printers, and 
* a command-line tool.

## An Example Model For Processing Customer Orders

```
package de.monticore.bpmn.readMeExample;

import de.monticore.bpmn.readMeExample.OrderToDelivery.*;

process OrderToDeliveryWorkflow {
    
    data order:Order;
    data checker:InventoryAvailabilityChecker;
    store agreement:CustomerDeliveryAgreement;
    
    lane Sales{
        service task ProcessOrder{
          boundary event PossibleCancellation 
            receive with RollbackOrderProcessing;
        }
        service task CheckProductAvailability
          count [order.numberOfOrderedProducts] parallel  
          {
            in order:Order;
          }
        send task CancellationMessage;
        service task RollbackOrderProcessing;

        split xor OrderFulfillable;
        merge xor FinishOrderProcessing;
        
        start event ReceiveOrder receive;
        event CancelOrder send
          compensate ProcessOrder;
    }
    
    lane Warehouse{
        
        manual task PrepareAndPackProducts;
        manual task PickUpOrder;
        event OrderDelivered;

        subprocess ShipOrder{
          manual task AttachLabelToPacket;
          manual task SecurePackageWithTape{
            resources = TapeDispenser;
          }
          service task SelectShippingCarrier;
          service task PrintShippingLabel{
            webservice = ##unspecified;
            operation = GetAddress;
          }
          
          operation GetAddress( 
            in customerID;
            out address
            );
          message customerID:String;
          message address:DestinationAddress;

          start event PrepareForShipment;
          end event ShipmentDispatched;

          PrepareForShipment -> SecurePackageWithTape 
            -> SelectShippingCarrier -> PrintShippingLabel 
            -> AttachLabelToPacket -> ShipmentDispatched;
        }

        end event OrderCompleted;
    }  
    
    ReceiveOrder -> ProcessOrder -> CheckProductAvailability -> OrderFulfillable ->
      { [checker.allProductsAvailable] PrepareAndPackProducts -> { [agreement.isOrderPickedUp] PickUpOrder;
                                                                   [_] ShipOrder;
                                                                 } -> OrderDelivered;
        [!checker.allProductsAvailable] CancellationMessage -> CancelOrder; 
      } -> FinishOrderProcessing -> OrderCompleted;

}
```
* The upper example model specifies a process named `OrderToDeliveryWorkflow`.
* The process distinguishes between two separate lanes. The `Sales` lane covers intake, processing and a possible cancellation of the order, while the `Warehouse` lane represents the packing and shipment.
  * `Sales` lane:
    * The process begins with the `ReceiveOrder` start event.
    * The `ProcessOrder` service task handles the main processing of the order. 
      A boundary event named `PossibleCancellation` is attached to the service task, enabling the process to react to a cancellation request by triggering the `RollbackOrderProcessing` service task.
    * The `CheckProductAvailability` service task uses a multi-instance loop to check the availability of each ordered product in parallel.
    * The exclusive gateway `OrderFulfillable` evaluates whether all ordered products are available. 
      * If all products are available, the process continues in the `Warehouse` lane.
      * If at least one product is unavailable, the send task `CancellationMessage` is executed. Subsequently, the intermediate event `CancelOrder` triggers compensation for the `ProcessOrder` task.
  * `Warehouse` lane:
    * The `PrepareAndPackProducts` manual task is responsible for preparing and packing the available products.
    * Depending on the delivery agreement, the process either continues with the `PickUpOrder` manual task or proceeds to the `ShipOrder` subprocess. The subprocess covers all shipping-related activities.
    * The process marks the order as delivered with the `OrderDelivered` intermediate event.
    * The workflow concludes with the `OrderCompleted` end event, indicating that the order has been successfully processed or appropriately canceled.
* Furthermore, the process contains data objects such as `order:Order` and `checker:InventoryAvailabilityChecker`, as well as a data store `agreement:CustomerDeliveryAgreement`, to manage and persist relevant information throughout the process.


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


