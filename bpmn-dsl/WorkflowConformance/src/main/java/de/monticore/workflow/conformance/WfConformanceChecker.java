package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.analysis.WfElementVisitor;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGenerator;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGraph;
import de.monticore.workflow.conformance.datastructure.ctl.PredicateGenerator;
import java.util.Set;
import java.util.function.Predicate;

public class WfConformanceChecker {

  private DummyIncarnationStrategy incStrategy;

  /** procedure to check if a node conform */
  public boolean checkConformance(
      ASTWorkflowCompilationUnit concrete, ASTWorkflowCompilationUnit reference) {

    // transform reference and concrete node
    IDWfNode ref = generateNode(reference, "Ref:");
    IDWfNode con = generateNode(reference, "con:");

    Predicate<Set<IDWfNode>> refPredicate = PredicateGenerator.postPredicate(ref);
    CTLGraph conGraph = new CTLGenerator().buildCTL(concrete);

    return conGraph.checkPredicate(refPredicate);
  }

  public IDWfNode generateNode(ASTWorkflowCompilationUnit ast, String prefix) {

    IDWfNodeBuilder builder = new IDWfNodeBuilder(prefix);

    // traverse the Workflow ast a collect elements
    WfElementVisitor collector = new WfElementVisitor(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    return builder.build();
  }
}
