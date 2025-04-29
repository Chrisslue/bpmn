 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.analysis.graph.WorkflowGraphConverter;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import org.jgrapht.Graph;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

public abstract class ProcessGraphCoCo implements WorkflowASTWFProcessCoCo {

  protected Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph;

  protected ASTWFProcess process;

  @Override
  public void check(final ASTWFProcess process) {
    ImmutableGraph<ASTFlowElement> graph = new WorkflowGraphConverter(process).convert().getGraph();

    this.processGraph = new ImmutableGraphAdapter<>(graph);
    this.process = process;

    check(new ImmutableGraphAdapter<>(graph), process);
  }

  protected abstract void check(
      final Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph,
      final ASTWFProcess process);
}
