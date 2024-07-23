package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.workflow.conformance.datastructure.BPMNNode;
import de.monticore.workflow.conformance.datastructure.BPMNNodeType;
import de.monticore.workflow.conformance.utils.BPMNElementStorage;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;

import java.util.*;

public class WF2LTSGenerator {

  private BPMNElementStorage storage;
  private  LTSBuilder ltsBuilder ;
  private BPMNNode startEvent;
  private BPMNNode endNode;


  /***
   * transform a BPMN to a sequence of scope, as scope we have sequence, Xor, Loop ,Or, and And.
   * @param wf the workflow as AST.
   */
  public BPMNNode bpmn2lts(ASTWorkflowCompilationUnit wf) {
    storage = new BPMNElementStorage(wf);
    ltsBuilder = new LTSBuilder();
    ASTEvent startEvent = BPMNUtils.getStartEvent(wf);

    List<BPMNNode> sequence = new ArrayList<>();
    sequence.add(ltsBuilder.mkEvent(startEvent.getName()));

    ASTFlowElement next = startEvent;
    while (storage.hasNext(next)){
      next = storage.getNext(next);
         sequence.add(mkNode(next));
    }

    return ltsBuilder.mkSequence(sequence);
  }



  BPMNNode mkNode(ASTFlowElement node) {


    if (node instanceof ASTTask){
      return  ltsBuilder.mkTask(((ASTTask) node).getName());
    } else if (node instanceof  ASTSequenceFlow) {
      List<BPMNNode> children = new ArrayList<>();

      for (ASTFlowTarget tgt: ((ASTSequenceFlow) node).getPathList()){
        ASTFlowElement element = storage.getElement(tgt.getNodeRef().getBaseName());
      }

      return ltsBuilder.mkSequence(children);

    }


   else if (node instanceof  ASTNamedGateway){
      List<BPMNNode> children = new ArrayList<>();

   BPMNNodeType type = BPMNUtils.getGatewayType((ASTNamedGateway) node);

    if (BPMNUtils.isSplit((ASTNamedGateway) node)){
     ((ASTNamedGateway) node).forEachOutgoings( n-> children.add(mkNode((ASTFlowElement) n)));
    }

    switch (type){
      case XOR: return ltsBuilder.mkXor(((ASTNamedGateway) node).getName(),children);
        case OR:return  ltsBuilder.mkOr(((ASTNamedGateway) node).getName(),children);
        case AND: return  ltsBuilder.mkAnd(((ASTNamedGateway) node).getName(),children);
        default:
        Log.error("unsupported gateway type: "+type);
        return  null ;
    }
    }

   return  null ;

  }




}
