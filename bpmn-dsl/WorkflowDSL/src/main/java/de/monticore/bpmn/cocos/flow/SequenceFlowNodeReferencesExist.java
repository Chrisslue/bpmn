package de.monticore.bpmn.cocos.flow;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTFlowTarget;
import de.monticore.bpmn.workflow._ast.ASTQName;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowTargetCoCo;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.se_rwth.commons.logging.Log;

public class SequenceFlowNodeReferencesExist implements WorkflowASTFlowTargetCoCo {

    @Override
    public void check(final ASTFlowTarget target) {
        target.getNodeRefOpt().map(ASTQName::getQualifiedName).ifPresent(name -> {
            WorkflowScope scope = (WorkflowScope) target.getEnclosingScope();

            if (!scope.resolveFlowNodeDown(name).isPresent()) {
                Log.error(Messages.get("0xWFM1004", name), target.get_SourcePositionStart());
            }
        });
    }

}
