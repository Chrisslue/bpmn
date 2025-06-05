package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTConstantsWorkflow;
import de.monticore.bpmn.workflow._ast.ASTWFSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * If a SubProcess is defined as ad-hoc, then the non-terminal AdHocCharacteristics must be specified.
 */

public class AdHocSubProcessHasAdHocCharacteristics implements WorkflowASTWFSubProcessCoCo {
    @Override
    public void check(final ASTWFSubProcess subProcess) {
        if(subProcess.getType() == ASTConstantsWorkflow.ADHOC){
            if(!subProcess.isPresentAdHocCharacteristics()){
                Log.error(Messages.get("0xWFM4004", subProcess.getName()));
            }
        }
    }

}
