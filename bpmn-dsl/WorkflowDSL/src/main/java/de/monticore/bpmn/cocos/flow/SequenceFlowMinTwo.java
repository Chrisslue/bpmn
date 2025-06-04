package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTSequenceFlow;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SequenceFlowMinTwo implements WorkflowASTWFProcessCoCo {
    @Override
    public void check(final ASTWFProcess flow) {
        List <ASTFlowElement> allElements = flow.getFlowElementList();
        List<ASTSequenceFlow> sequenceFlows = new ArrayList<>() {
        };

        for (ASTFlowElement element : allElements) {
            if (element instanceof ASTSequenceFlow) {
                sequenceFlows.add((ASTSequenceFlow) element);
            }
        }

        for(ASTSequenceFlow element: sequenceFlows){
            if(element.sizePath() < 2){
                Log.error(Messages.get("0xWFM3006"));
            }
        }
    }

}
