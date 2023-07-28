package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.trafos.AddNameToInlineFlowNodes;
import de.monticore.bpmn.trafos.AddReferenceToParentLane;
import de.monticore.bpmn.trafos.AddSequenceFlowToFlowNodes;
import de.monticore.bpmn.trafos.CreateIOSpecification;
import de.monticore.bpmn.trafos.SetSubProcessTriggeredByEvent;
import de.monticore.bpmn.wf2lts.collector.StartEventCollector;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import de.monticore.bpmn.wf2lts.transformer.DefaultGraph2LTSTransformer;
import de.monticore.bpmn.wf2lts.transformer.Graph2LTSTransformer;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.lts.LTSBuilder;

public class WF2LTSGenerator {

  private static ASTEvent getStartEvent(ASTWorkflowCompilationUnit ast) {

    var traverser = WorkflowMill.traverser();
    var startEventCollector = new StartEventCollector(traverser);
    for (ASTFlowElement astFlowElement : ast.getProcess().getFlowElementList()) {
      astFlowElement.accept(traverser);
    }
    var startEvents = startEventCollector.getEvents();
    if (startEvents.size() != 1) {
      throw new IllegalArgumentException("Workflow had more than one start element");
    }
    return startEvents.get(0);
  }

  /* Conversion to LTL
   * 1. Build an intermediate graph representing the flow structure.
   *    All gateways and subprocesses are represented by meta constructs.
   * 2. Transform ASTFlowNode's to Strings using a NamingStrategy.
   * 3. Resolve meta constructs:
   *    3.1 Subprocesses connect outer graph to start events and end events back to the graph.
   *    3.2 GatewayScopes use an interleaving strategy to combine the different paths.
   * 4. Transform graph of task/event-nodes to an LTL structure.
   * 5. Use the LTLBuilder Interface for further usage.
   */

  public static ASTWorkflowCompilationUnit loadBPMN(String modelFile) {
    // Setup
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast = tool.parse(modelFile);
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddReferenceToParentLane().transform(ast);
    new CreateIOSpecification().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    return ast;
  }

  public static LTS workflow2LTS(String modelFile) {
    return workflow2LTS(loadBPMN(modelFile));
  }

  public static IntermediateGraphWithScopes transformToGraph(ASTWorkflowCompilationUnit ast) {
    var startEvent = getStartEvent(ast);
    return GraphBuildingTraverser.graphOf(startEvent);
  }

  public static LTS workflow2LTS(ASTWorkflowCompilationUnit ast) {
    return workflow2LTS(ast, new DefaultGraph2LTSTransformer());
  }

  public static LTS workflow2LTS(ASTWorkflowCompilationUnit ast, Graph2LTSTransformer graph2LTSTransformer) {
    var lts = graph2LTSTransformer.transform(transformToGraph(ast));
    removeUnreachable(lts);
    return lts;
  }

  public static LTSWithFinalStates workflow2LTSWithFinalStates(
      ASTWorkflowCompilationUnit ast,
      Graph2LTSTransformer graph2LTSTransformer
  ) {
    // Mark every terminal state as final state assuming terminal states are those where and end events points to.
    return LTSWithFinalStates.ofTerminalStates(workflow2LTS(ast, graph2LTSTransformer));
  }

  public static <S, L, Builder extends LTSBuilder<S, L>> Builder workflow2LTS(
      ASTWorkflowCompilationUnit ast,
      Builder builder
  ) {
    return workflow2LTS(ast).toModel(builder);
  }

  private static void removeUnreachable(LTS lts) {
    var stateSize = 0;
    do {
      stateSize = lts.getStates().size();
      lts.getStates().stream()
          .filter(state -> state != lts.getStart() && lts.getIncoming(state).isEmpty())
          .forEach(lts::removeState);
    } while (lts.getStates().size() != stateSize);
  }

}
