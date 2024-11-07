package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.utils.DummyVisitor;
import java.util.List;

public class DummyIncarnationStrategy {
  private List<String> concreteNames;
  private List<String> referenceNames;

  public DummyIncarnationStrategy(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    // traverse the Workflow ast a collect elements
    DummyVisitor collector = new DummyVisitor();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    concrete.accept(traverser);
    this.concreteNames = collector.getTaskAndEventNames();

    collector = new DummyVisitor();
    traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    reference.accept(traverser);
    this.referenceNames = collector.getTaskAndEventNames();
  }

  public boolean isIncarnation(WfNode concrete) {
    boolean res = referenceNames.contains(concrete.getLabel().split(":")[1]);
    return res;
  }

  public boolean ignore(WfNode node) {
    return node.getLabel().startsWith("Concrete") && !isIncarnation(node);
  }

  public boolean checkIncarnation(WfNode concrete, WfNode reference) {
    return concrete.getLabel().split(":")[1].equals(reference.getLabel().split(":")[1]);
  }
}
