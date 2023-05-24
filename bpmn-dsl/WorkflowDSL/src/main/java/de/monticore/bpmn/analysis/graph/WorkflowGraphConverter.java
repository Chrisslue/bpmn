package de.monticore.bpmn.analysis.graph;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import de.monticore.bpmn.visitors.WorkflowLocalVisitor;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

/**
 * Creates an explicit control flow representation of a BPMN process or sub-process.
 *
 * <p>Handles a single process level at a time. Contained sub-processes must be handled separately.
 */
public class WorkflowGraphConverter extends WorkflowLocalVisitor {

  // prefer: https://github.com/google/guava/wiki/GraphsExplained#basic-graph-example
  // requires more recent guava version (conflicts with MC version)
  private MutableGraph<ASTFlowNode> graph;

  public WorkflowGraphConverter(ASTFlowElementContainer localRoot) {
    super(localRoot);
  }

  /**
   * Returns the control flow graph of the process or sub-process.
   *
   * @return the control flow graph of the process or sub-process
   */
  public ImmutableGraph<ASTFlowNode> getGraph() {
    return ImmutableGraph.copyOf(graph);
  }

  /**
   * Creates the control flow graph of the process or sub-process.
   *
   * @return this
   */
  public WorkflowGraphConverter convert() {
    graph = GraphBuilder.directed().build();
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(this);
    localRoot.accept(traverser);

    return this;
  }

  @Override
  public void visit(final ASTActivity activity) {
    activity.getBoundaryEvents().forEach(boundaryEvent -> graph.putEdge(activity, boundaryEvent));
  }

  @Override
  public void visit(final ASTFlowNode flowNode) {
    if (flowNode.equals(localRoot)) { // sub-process is handled in parent execution
      return;
    }

    graph.addNode(flowNode);

    flowNode
        .streamIncomings()
        .map(SequenceFlow::getSource)
        .forEach(source -> graph.putEdge(source, flowNode));
    flowNode
        .streamOutgoings()
        .map(SequenceFlow::getTarget)
        .forEach(target -> graph.putEdge(flowNode, target));
  }
}
