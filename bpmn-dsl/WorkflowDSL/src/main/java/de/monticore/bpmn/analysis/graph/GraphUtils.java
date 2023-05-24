package de.monticore.bpmn.analysis.graph;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.guava.ImmutableGraphAdapter;

/** Graph utils. */
public class GraphUtils {

  public static Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraphFrom(
      final ASTFlowElementContainer container) {
    final ImmutableGraph<ASTFlowNode> graph =
        new WorkflowGraphConverter(container).convert().getGraph();

    return new ImmutableGraphAdapter<>(graph);
  }
}
