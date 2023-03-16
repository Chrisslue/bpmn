package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProcessHasNoDisconnectedComponents extends ProcessGraphCoCo {

    @Override
    public void check(final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph, final ASTFlowElementContainer process) {
        if (process.isEmptyFlowElements()) { // allow empty processes (an empty process is disconnected by definition)
            return;
        }
        ConnectivityInspector<ASTFlowNode, EndpointPair<ASTFlowNode>> inspector = new ConnectivityInspector<>(processGraph);
        List<Set<ASTFlowNode>> components = inspector.connectedSets()
                .stream()
                .filter(component -> !isEventSubProcess(component) && !isCompensationActivity(component))
                .collect(Collectors.toList());
        if (components.size() > 1) {
            final String formattedComponents = components.stream()
                    .map(set -> "{"
                            + set.stream().map(ASTFlowNode::getName).sorted().collect(Collectors.joining(", "))
                            + "}"
                    )
                    .collect(Collectors.joining(", "));

            Log.warn(Messages.get("0xWFM7010", formattedComponents), process.get_SourcePositionStart());
        }
    }

    private boolean isEventSubProcess(final Set<ASTFlowNode> nodes) {
        if(!nodes.isEmpty()){
            IsEventSubProcessVisitor visitor = new IsEventSubProcessVisitor();

            WorkflowTraverser traverser = WorkflowMill.traverser();
            traverser.setWorkflowHandler(visitor);
            nodes.stream().findFirst().get().accept(traverser);
            return visitor.isEventSubProcess();
        }
        return false;
    }

    private boolean isCompensationActivity(final Set<ASTFlowNode> nodes) {
        IsForCompensationVisitor visitor = new IsForCompensationVisitor();

        WorkflowTraverser traverser = WorkflowMill.traverser();
        traverser.setWorkflowHandler(visitor);
        nodes.stream().findFirst().get().accept(traverser);
        return visitor.isForCompensation();
    }

}
