package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTEventTriggerCompensate;
import de.monticore.bpmn.workflow._cocos.WorkflowASTEventTriggerCompensateCoCo;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.se_rwth.commons.logging.Log;

import static com.google.common.base.Preconditions.checkArgument;

public class CompensatedActivityExists implements WorkflowASTEventTriggerCompensateCoCo {

    @Override
    public void check(ASTEventTriggerCompensate node) {
        checkArgument(node.getEnclosingScope() != null);

        if (node.isPresentActivity()) {
            WorkflowScope enclosingScope = (WorkflowScope) node.getEnclosingScope();

            if (!enclosingScope.resolveActivityLocally(node.getActivity()).isPresent()) {
                Log.error(Messages.get("0xWFM1005", node.getActivity()),
                        node.get_SourcePositionStart(), node.get_SourcePositionEnd());
            }
        }
    }

}
