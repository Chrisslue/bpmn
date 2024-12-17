package de.monticore.bpmn.analysis.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import org.jgrapht.Graph;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

/** Graph utils. */
public class GraphUtils {

  public static Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraphFrom(
      final ASTProcess container) {
    final ImmutableGraph<ASTFlowElement> graph =
        new WorkflowGraphConverter(container).convert().getGraph();

    return new ImmutableGraphAdapter<>(graph);
  }
}
