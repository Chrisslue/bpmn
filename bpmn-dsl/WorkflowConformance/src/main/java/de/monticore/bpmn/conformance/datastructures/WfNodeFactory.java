package de.monticore.bpmn.conformance.datastructures;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/***
 * This class take as parameter a workflowBuilder.
 * It visits a workflow element
 * and use the builder to transform workflow elements (tasks, events, gateways, etc.)
 * in to Node.
 * The builder also collects the sequence flows.
 */
public class WfNodeFactory implements WorkflowVisitor2 {

  public static WfBuilder buildWorkflowNodes(ASTWorkflowCompilationUnit ast, WfBuilder builder) {

    // traverse the Workflow asts a collect elements
    WfNodeFactory collector = new WfNodeFactory(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    return builder;
  }


  private final WfBuilder builder;

  public WfNodeFactory(WfBuilder builder) {
    this.builder = builder;
  }

  @Override
  public void visit(ASTNamedEvent node) {
    if (node.isStart()) {
      builder.mkStartEvent(node);
    } else if (node.isEnd()) {
      builder.mkEndEvent(node);
    } else {
      builder.mkNamedEvent(node);
    }
  }

  @Override
  public void visit(ASTTask node) {
    builder.mkNamedTask(node);
  }

  @Override
  public void visit(ASTSequenceFlow node) {
    builder.mkSequence(node);
  }

  @Override
  public void visit(ASTNamedGateway node) {
    builder.mkNamedGateway(node);
  }


  @Override
  public void endVisit(ASTWorkflowCompilationUnit wf) {
    builder.build();
  }
}
