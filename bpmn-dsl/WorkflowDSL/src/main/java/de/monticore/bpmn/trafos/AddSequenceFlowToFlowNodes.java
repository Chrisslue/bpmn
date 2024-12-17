package de.monticore.bpmn.trafos;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.List;
import one.util.streamex.StreamEx;

/** Adds references to the incoming and outgoing sequence flows to the flow nodes. */
public class AddSequenceFlowToFlowNodes extends WorkflowTransformation implements WorkflowVisitor2 {

  @Override
  protected void transform() {
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(this);
    getAst().accept(traverser);
  }

  @Override
  public void visit(final ASTSequenceFlow flow) {
    handlePath(flow.getFlowBranch().getPathList());
  }

  @Override
  public void visit(final ASTFlowBranch branch) {
    handlePath(branch.getPathList());
  }

  private void handlePath(final List<ASTFlowTarget> path) {
    StreamEx.of(path)
        .forPairs(
            (from, to) -> {
              // connect each source to each target (multiple sources/targets possible for flow
              // blocks)
              from.asSource()
                  .forEach(
                      source ->
                          to.asTarget()
                              .entries()
                              .forEach(
                                  entry -> {
                                    ASTFlowElement target = entry.getKey();
                                    List<ASTFlowCondition> conditions = entry.getValue();

                                    final SequenceFlow flow =
                                        new SequenceFlowBuilder()
                                            .setSource(source)
                                            .setTarget(target)
                                            .addConditions(conditions)
                                            .build();

                                    source.addOutgoings(flow);
                                    target.addIncomings(flow);
                                  }));
            });
  }
}
