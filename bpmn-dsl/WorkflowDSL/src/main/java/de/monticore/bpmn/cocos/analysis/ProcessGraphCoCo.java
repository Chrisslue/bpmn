package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.analysis.graph.WorkflowGraphConverter;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._cocos.WorkflowASTFlowElementContainerCoCo;
import org.jgrapht.Graph;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

public abstract class ProcessGraphCoCo implements WorkflowASTFlowElementContainerCoCo {

    protected Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph;

    protected ASTFlowElementContainer process;

    @Override
    public void check(final ASTFlowElementContainer process) {
        ImmutableGraph<ASTFlowNode> graph = new WorkflowGraphConverter(process).convert().getGraph();

        this.processGraph = new ImmutableGraphAdapter<>(graph);
        this.process = process;

        check(new ImmutableGraphAdapter<>(graph), process);
    }

    abstract protected void check(final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph, final ASTFlowElementContainer process);

}
