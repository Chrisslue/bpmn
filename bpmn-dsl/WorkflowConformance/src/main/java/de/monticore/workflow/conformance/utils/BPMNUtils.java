package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.WfElementFactory;
import de.monticore.workflow.conformance.datastructure.interf.IDWfNodeBuilder;
import java.util.function.Function;

public class BPMNUtils {

  public static IDWfNodeBuilder generateIDWfNode(
      ASTWorkflowCompilationUnit ast, Function<String, String> identifier) {

    IDWfNodeBuilder builder = new IDWfNodeBuilder(identifier);

    // traverse the Workflow ast a collect elements
    WfElementFactory collector = new WfElementFactory(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    builder.build(); // todo handle that differently

    return builder;
  }
}
