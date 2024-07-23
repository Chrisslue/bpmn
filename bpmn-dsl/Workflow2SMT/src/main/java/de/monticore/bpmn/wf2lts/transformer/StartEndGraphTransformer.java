package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.EdgeTo;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowCondition;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTInlineEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Split every intermediate-event and task A, to A_Start and A_End. It is important that meta
 * elements such as gateways and subprocesses as well as non-intermediate events are not split as
 * this would break other transformer. How the split start and end elements should be named can be
 * set through the startNameSuffix and endNameSuffix.
 */
public class StartEndGraphTransformer extends DefaultGraph2LTSTransformer {

  private final String startNameSuffix;
  private final String endNameSuffix;

  public StartEndGraphTransformer() {
    this("_S", "_E");
  }

  public StartEndGraphTransformer(String startNameSuffix, String endNameSuffix) {
    super();
    this.startNameSuffix = startNameSuffix;
    this.endNameSuffix = endNameSuffix;
  }

  public StartEndGraphTransformer(
      String startNameSuffix,
      String endNameSuffix,
      NamingStrategy<ASTFlowNode> namingStrategy,
      GatewayTransformer gatewayTransformer,
      SubprocessTransformer subprocessTransformer) {
    super(namingStrategy, gatewayTransformer, subprocessTransformer);
    this.startNameSuffix = startNameSuffix;
    this.endNameSuffix = endNameSuffix;
  }

  private String startName(String name) {
    return name + startNameSuffix;
  }

  private String endName(String name) {
    return name + endNameSuffix;
  }

  private void convertNodes(GraphSpecificParameter parameter) {
    var graph = parameter.graph;
    var allNodes =
        Stream.concat(
                graph.getEdges().keySet().stream(),
                graph.getEdges().values().stream()
                    .flatMap(Collection::stream)
                    .map(EdgeTo::getTarget))
            .distinct()
            .collect(Collectors.toList());
    var visitor = new NodeSplittingVisitor(parameter);
    var traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    for (ASTFlowNode node : allNodes) {
      node.accept(traverser);
      if (!parameter.taskStart.containsKey(node) && !parameter.eventStart.containsKey(node)) {
        parameter.atomicNodes.put(node, new State());
      }
    }
  }

  private State getStart(ASTFlowNode node, GraphSpecificParameter parameter) {
    if (parameter.taskStart.containsKey(node)) {
      return parameter.taskStart.get(node);
    }
    if (parameter.eventStart.containsKey(node)) {
      return parameter.taskStart.get(node);
    }
    return parameter.atomicNodes.get(node);
  }

  private State getEnd(ASTFlowNode node, GraphSpecificParameter parameter) {
    if (parameter.taskEnd.containsKey(node)) {
      return parameter.taskEnd.get(node);
    }
    if (parameter.eventEnd.containsKey(node)) {
      return parameter.taskEnd.get(node);
    }
    return parameter.atomicNodes.get(node);
  }

  private Transition connectNonAtomicStartEnd(
      ASTFlowNode node, Map<ASTFlowNode, State> start, Map<ASTFlowNode, State> end) {
    return new Transition(
        start.get(node),
        Collections.emptyList(),
        endName(super.namingStrategy.apply(node)),
        end.get(node));
  }

  private void addManualEdgeToDanglingNodes(GraphSpecificParameter params) {
    var graph = params.graph;
    var lts = params.lts;

    // Collect all nodes that don't have any incoming edge.
    var danglingNodes =
        Stream.concat(
                graph.getEdges().keySet().stream(),
                graph.getEdges().values().stream().flatMap(List::stream).map(EdgeTo::getTarget))
            .distinct()
            .filter(node -> graph.predecessorNodes(node).isEmpty())
            .collect(Collectors.toList());

    for (var dangling : danglingNodes) {
      // The start state of the graph has to be handled differently
      var sourceState = dangling == graph.getStart() ? lts.getStart() : new State();
      params.lts.addTransition(
          convertToTransition(sourceState, dangling, Collections.emptyList(), params));
    }
  }

  private Transition convertToTransition(
      State sourceState,
      ASTFlowNode target,
      List<ASTFlowCondition> conditions,
      GraphSpecificParameter params) {
    var targetStart = getStart(target, params);
    var label = namingStrategy.apply(target);
    if (targetStart != getEnd(target, params)) {
      label = startName(label); // If the target is non-atomic we use the start-name
    }
    return new Transition(sourceState, conditions, label, targetStart);
  }

  private void convertEdgeToTransition(GraphSpecificParameter params) {
    var graph = params.graph;
    for (var entry : graph.getEdges().entrySet()) {
      var sourceState = getEnd(entry.getKey(), params);
      for (var edgeTo : entry.getValue()) {
        params.lts.addTransition(
            convertToTransition(sourceState, edgeTo.getTarget(), edgeTo.getConditions(), params));
      }
    }
  }

  private void convertEdges(GraphSpecificParameter params) {
    var lts = params.lts;

    // Add manual transition to all nodes which have no incoming edges.
    addManualEdgeToDanglingNodes(params);

    // Connect the non-atomic nodes (create the transition from start to end)
    for (var task : params.taskStart.keySet()) {
      lts.addTransition(connectNonAtomicStartEnd(task, params.taskStart, params.taskEnd));
    }
    for (var event : params.eventStart.keySet()) {
      lts.addTransition(connectNonAtomicStartEnd(event, params.eventStart, params.eventEnd));
    }
    // Translate edges to transitions by pushing target names to incoming transitions.
    convertEdgeToTransition(params);
  }

  @Override
  protected LTS nodeBasedToTransitionBased(IntermediateGraphWithScopes graph) {
    var parameter = new GraphSpecificParameter(graph);
    convertNodes(parameter);
    convertEdges(parameter);
    return parameter.lts;
  }

  private static class NodeSplittingVisitor implements WorkflowVisitor2 {

    private final GraphSpecificParameter parameter;

    public NodeSplittingVisitor(GraphSpecificParameter parameter) {
      this.parameter = parameter;
    }

    @Override
    public void visit(ASTTask node) {
      parameter.taskStart.put(node, new State());
      parameter.taskEnd.put(node, new State());
    }

    private void addIfInterMediate(ASTEvent event) {
      if (!event.isIntermediate() || event.isBoundary()) {
        return;
      }
      parameter.eventStart.put(event, new State());
      parameter.eventEnd.put(event, new State());
    }

    @Override
    public void visit(ASTInlineEvent node) {
      addIfInterMediate(node);
    }

    @Override
    public void visit(ASTNamedEvent node) {
      addIfInterMediate(node);
    }
  }

  private static class GraphSpecificParameter {

    private final Map<ASTFlowNode, State> taskStart;
    private final Map<ASTFlowNode, State> taskEnd;
    private final Map<ASTFlowNode, State> eventStart;
    private final Map<ASTFlowNode, State> eventEnd;
    private final Map<ASTFlowNode, State> atomicNodes;

    private final IntermediateGraphWithScopes graph;

    private final LTS lts;

    public GraphSpecificParameter(IntermediateGraphWithScopes graph) {
      taskStart = new HashMap<>();
      taskEnd = new HashMap<>();
      eventStart = new HashMap<>();
      eventEnd = new HashMap<>();
      atomicNodes = new HashMap<>();
      this.graph = graph;
      this.lts = new LTS();
    }
  }
}
