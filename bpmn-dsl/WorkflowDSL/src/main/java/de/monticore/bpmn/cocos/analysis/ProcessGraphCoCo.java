package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.analysis.graph.WorkflowGraphConverter;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import org.jgrapht.Graph;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

public abstract class ProcessGraphCoCo implements WorkflowASTProcessCoCo {

  protected Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph;

  protected ASTProcess process;

  @Override
  public void check(final ASTProcess process) {
    ImmutableGraph<ASTFlowElement> graph = new WorkflowGraphConverter(process).convert().getGraph();

    this.processGraph = new ImmutableGraphAdapter<>(graph);
    this.process = process;

    check(new ImmutableGraphAdapter<>(graph), process);
  }

  protected abstract void check(
      final Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph,
      final ASTProcess process);
}
