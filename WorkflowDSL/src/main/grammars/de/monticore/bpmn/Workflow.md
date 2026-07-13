<!-- (c) https://github.com/MontiCore/monticore -->
# Business Process Model and Notation (BPMN)
The purpose of this language is to provide a textual alternative to graphical **BPMN** modeling.

The BPMN language consists of 
* the grammar`Workflow`
* the component grammar `TimerConditions`
* context conditions,
* pretty printers, and 
* a command-line tool.

## An Example Model For Processing Customer Orders

```
 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.examples;

import de.monticore.bpmn.cds.OrderToDelivery.*;

process OrderToDeliveryWorkflow {
    
    data order:Order;
    data checker:InventoryAvailabilityChecker;
    data agreement:CustomerDeliveryAgreement;
    store products:Product;

    message cancelMsg:String;
    operation prepCancelMsg(
      in cancelMsg;
      out cancelMsg
    );
    
    lane Sales{
        service task ProcessOrder{
          webservice = ##webservice;
          boundary event PossibleCancellation 
            catch compensate ProcessOrder with RollbackOrderProcessing;
        }
        service task CheckProductAvailability
          count [order.numberOfOrderedProducts] parallel
          {
            webservice = ##webservice;
            in order:Order;
          }
        send task SendCancellationMessage{
          webservice = ##webservice;
          operation = prepCancelMsg;
          message = cancelMsg;
        }
        service task RollbackOrderProcessing {
          webservice = ##webservice;
        }

        split xor OrderFulfillable;
        merge xor FinishOrderProcessing;
        
        start event ReceiveOrder catch;
        event CancelOrder throw
          compensate ProcessOrder;
    }
    
    lane Warehouse{
        
        manual task PrepareAndPackProducts{
          resources = order, products;
        }
        manual task PickUpOrder{
          resources = order, products;
        }
        event OrderDelivered;

        subprocess ShipOrder{
          manual task AttachLabelToPacket{
            resources = order, products;
          }
          manual task SecurePackageWithTape{
            resources = TapeDispenser;
          }
          service task SelectShippingCarrier {
            webservice = ##webservice;
          }
          service task PrintShippingLabel{
            webservice = ##webservice;
            operation = getAddress;
          }
          
          operation getAddress(
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
    
    ReceiveOrder
      -> ProcessOrder
        -> CheckProductAvailability
          -> OrderFulfillable
              -> {
                 [checker.allProductsAvailable] PrepareAndPackProducts
                   -> split xor -> {
                        [agreement.isOrderPickedUp] PickUpOrder;
                        [_] ShipOrder;
                      } -> merge xor
                   -> OrderDelivered;
                 [!checker.allProductsAvailable] SendCancellationMessage
                   -> CancelOrder;
               }
            -> FinishOrderProcessing
              -> OrderCompleted;
}
```
* The example model above specifies a process named `OrderToDeliveryWorkflow`.
* The process distinguishes between two separate lanes. 
  The `Sales` lane covers intake, processing and a possible cancellation of
  the order, while the `Warehouse` lane represents the packing and shipment.
  * `Sales` lane:
    * The process begins with the `ReceiveOrder` start event.
    * The `ProcessOrder` service task handles the main processing of the order. 
      A boundary event named `PossibleCancellation` is attached to the 
      service task, enabling the process to react to a cancellation request
      by triggering the `RollbackOrderProcessing` service task.
    * The `CheckProductAvailability` service task uses a multi-instance loop
      to check the availability of each ordered product in parallel.
    * The exclusive gateway `OrderFulfillable` evaluates whether all ordered
      products are available. 
      * If all products are available, the process continues in the 
        `Warehouse` lane.
      * If at least one product is unavailable, the send task 
        `CancellationMessage` is executed. Subsequently, the intermediate 
        event `CancelOrder` triggers compensation for the `ProcessOrder` 
        task, thus leading to the task `RollbackOrderProcessing`.
  * `Warehouse` lane:
    * The `PrepareAndPackProducts` manual task is responsible for preparing
      and packing the available products.
    * Depending on the delivery agreement, the process either continues with
      the `PickUpOrder` manual task or proceeds to the `ShipOrder` subprocess. 
      The subprocess covers all shipping-related activities.
      * To complete the manual task `SecurePackageWithTape`, the resource 
        `TapeDispenser` is used.
      * The service task `PrintShippingLabel` utilizes an unspecified web 
        service and invokes the operation `GetAddress`, which retrieves the
        destination `address` for corresponding customer. 
        The operation takes the `customerID` as input and returns the 
        appropriate `address` as output, with both parameters defined as
        messages.
    * The process marks the order as delivered with the `OrderDelivered`
      intermediate event.
    * The workflow concludes with the `OrderCompleted` end event, indicating
      that the order has been successfully processed or appropriately canceled.
* Furthermore, the process contains data objects such as `order:Order` and
  `checker:InventoryAvailabilityChecker`, as well as a data store 
  `agreement:CustomerDeliveryAgreement`, to manage and persist relevant 
  information throughout the process.


## Context Conditions (CoCos)

This sections lists the context conditions for the BPMN language.


Context conditions for activities:

* [```AdHocSubProcessContainsAtLeastOneActivity```](../../../../java/de/monticore/bpmn/cocos/activities/AdHocSubProcessContainsAtLeastOneActivity.java)  

* [```AdHocSubProcessHasNoStartAndEndEvent```](../../../../java/de/monticore/bpmn/cocos/activities/AdHocSubProcessHasNoStartAndEndEvent.java)  

* [```CalledElementDoesExist```](../../../../java/de/monticore/bpmn/cocos/activities/CalledElementDoesExist.java) 

* [```CompensationActivityHasNoIncomingOrOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/activities/CompensationActivityHasNoIncomingOrOutgoingFlow.java)  

* [```EventSubProcessHasNoIncomingOrOutgoingFlow```](../../../../java/de/monticore/bpmn/cocos/activities/EventSubProcessHasNoIncomingOrOutgoingFlow.java)  
  is not yet implemented.

* [```EventSubProcessHasOnlyOneStartEvent```](../../../../java/de/monticore/bpmn/cocos/activities/EventSubProcessHasOnlyOneStartEvent.java)  

* [```LoopCountExpressionReturnsIntegerNumber```](../../../../java/de/monticore/bpmn/cocos/activities/LoopCountExpressionReturnsIntegerNumber.java)  

* [```TaskContainsOnlyBoundaryEvents```](../../../../java/de/monticore/bpmn/cocos/activities/TaskContainsOnlyBoundaryEvents.java)  
  is not yet implemented.



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

* [```BoundaryEventHasNoIncomingFlow```](../../../../java/de/monticore/bpmn/cocos/flow/BoundaryEventHasNoIncomingFlow.java)

* [```DefaultBranchIsLastBranch```](../../../../java/de/monticore/bpmn/cocos/flow/DefaultBranchIsLastBranch.java)

* [```DefaultFlowHasValidSource```](../../../../java/de/monticore/bpmn/cocos/flow/DefaultFlowHasValidSource.java)

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
  is not yet implemented.


## Serialization of Symbol Tables

The Workflow DSL uses the DeSer implementations as generated by MontiCore
without any handwritten extensions.
For example, the following the symbol file obtained from
serializing the symbol table instance depicted in 
"An Example Model for Processing Customer Orders":

```json
{
  "generated-using": "www.MontiCore.de technology",
  "name": "OrderToDeliveryWorkflow",
  "package": "de.monticore.bpmn.examples",
  "symbols": [
    {
      "kind": "de.monticore.bpmn.workflow._symboltable.WFProcessSymbol",
      "name": "OrderToDeliveryWorkflow",
      "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow",
      "packageName": "de.monticore.bpmn.examples",
      "spannedScope": {
        "symbols": [
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "ProcessOrder",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ProcessOrder"
            ,"packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "CheckProductAvailability",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.CheckProductAvailability",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "SendCancellationMessage",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.SendCancellationMessage",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "RollbackOrderProcessing",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.RollbackOrderProcessing",
            "packageName": "de.monticore.bpmn.examples",
            "compensating": true
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "PrepareAndPackProducts",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.PrepareAndPackProducts",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
            "name": "PickUpOrder",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.PickUpOrder",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFSubProcessSymbol",
            "name": "ShipOrder",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder",
            "packageName": "de.monticore.bpmn.examples",
            "spannedScope": {
              "symbols": [
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
                  "name": "AttachLabelToPacket",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.AttachLabelToPacket",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
                  "name": "SecurePackageWithTape",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.SecurePackageWithTape",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
                  "name": "SelectShippingCarrier",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.SelectShippingCarrier",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFActivitySymbol",
                  "name": "PrintShippingLabel",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.PrintShippingLabel",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFOperationSymbol",
                  "name": "getAddress",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.getAddress",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
                  "name": "PrepareForShipment",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.PrepareForShipment",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
                  "name": "ShipmentDispatched",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.ShipmentDispatched",
                  "packageName": "de.monticore.bpmn.examples"
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFNotificationSymbol",
                  "name": "customerID",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.customerID",
                  "packageName": "de.monticore.bpmn.examples",
                  "isMessage": true,
                  "type": {
                    "kind": "de.monticore.types.check.SymTypeOfObject",
                    "objName": "String"
                  }
                },
                {
                  "kind": "de.monticore.bpmn.workflow._symboltable.WFNotificationSymbol",
                  "name": "address",
                  "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ShipOrder.address",
                  "packageName": "de.monticore.bpmn.examples",
                  "isMessage": true,
                  "type": {
                    "kind": "de.monticore.types.check.SymTypeOfObject",
                    "objName": "de.monticore.bpmn.cds.OrderToDelivery.DestinationAddress"
                  }
                }
              ]
            }
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFOperationSymbol",
            "name": "prepCancelMsg",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.prepCancelMsg",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFGatewaySymbol",
            "name": "OrderFulfillable",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.OrderFulfillable",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFGatewaySymbol",
            "name":"FinishOrderProcessing",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.FinishOrderProcessing",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
            "name": "PossibleCancellation",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.PossibleCancellation",
            "packageName": "de.monticore.bpmn.examples",
            "boundary": true
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
            "name": "ReceiveOrder",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.ReceiveOrder",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
            "name": "CancelOrder",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.CancelOrder",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
            "name": "OrderDelivered",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.OrderDelivered",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFEventSymbol",
            "name": "OrderCompleted",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.OrderCompleted",
            "packageName": "de.monticore.bpmn.examples"
          },
          {
            "kind": "de.monticore.bpmn.workflow._symboltable.WFNotificationSymbol",
            "name": "cancelMsg",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.cancelMsg",
            "packageName": "de.monticore.bpmn.examples",
            "isMessage": true,
            "type": {
              "kind": "de.monticore.types.check.SymTypeOfObject",
              "objName": "String"
            }
          },
          {
            "kind": "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol",
            "name": "order",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.order",
            "packageName": "de.monticore.bpmn.examples",
            "type": {
              "kind": "de.monticore.types.check.SymTypeOfObject",
              "objName": "de.monticore.bpmn.cds.OrderToDelivery.Order"
            }
          },
          {
            "kind": "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol",
            "name": "checker",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.checker",
            "packageName": "de.monticore.bpmn.examples",
            "type": {
              "kind": "de.monticore.types.check.SymTypeOfObject",
              "objName": "de.monticore.bpmn.cds.OrderToDelivery.InventoryAvailabilityChecker"
            }
          },
          {
            "kind": "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol",
            "name": "agreement",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.agreement",
            "packageName":"de.monticore.bpmn.examples",
            "type":{
              "kind": "de.monticore.types.check.SymTypeOfObject",
              "objName": "de.monticore.bpmn.cds.OrderToDelivery.CustomerDeliveryAgreement"
            }
          },
          {
            "kind": "de.monticore.symbols.basicsymbols._symboltable.VariableSymbol",
            "name": "products",
            "fullName": "de.monticore.bpmn.examples.OrderToDeliveryWorkflow.products",
            "packageName":"de.monticore.bpmn.examples",
            "type": { 
              "kind": "de.monticore.types.check.SymTypeOfObject",
              "objName": "de.monticore.bpmn.cds.OrderToDelivery.Product"
            }
          }
        ]
      }
    }
  ]
}
```
