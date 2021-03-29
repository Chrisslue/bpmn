package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._cocos.WorkflowASTActivityCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 302
 * Description: Compensation Association occurs outside the normal flow.
 */
public class CompensationActivityHasNoIncomingOrOutgoingFlow implements WorkflowASTActivityCoCo {

    @Override
    public void check(final ASTActivity activity) {
        if (activity.isForCompensation()) {
            activity.streamIncomings().map(SequenceFlow::getSource).forEach(source ->
                    Log.error(Messages.get("0xWFM6001", activity.getName()),
                            source.get_SourcePositionStart(), source.get_SourcePositionEnd()));
            activity.streamOutgoings().map(SequenceFlow::getTarget).forEach(target ->
                    Log.error(Messages.get("0xWFM6001", activity.getName()),
                            target.get_SourcePositionStart(), target.get_SourcePositionEnd()));
        }
    }

}