package de.monticore.wf2lts;

import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverserImplementation;

public class WF2LTLGenerator {

  private static ASTEvent getStartEvent(ASTWorkflowCompilationUnit ast) {

    var traverser = new WorkflowTraverserImplementation();
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

  public static ASTWorkflowCompilationUnit loadBPMN(String file) {
    // Setup
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast = tool.parse(file);
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddReferenceToParentLane().transform(ast);
    new CreateIOSpecification().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    var startEvent = getStartEvent(ast);
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    // 1. Building the intermediate graph.
    IntermediateGraph graph = GraphBuildingTraverser.graphOf(startEvent);
    System.out.println(graph);

    return ast;
  }
}
