package de.monticore.wf2smt;

import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.Optional;

public class WF2SMTGenerator {
  public static ASTWorkflowCompilationUnit loadBPMN(String file) {
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
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);
    return ast;
  }
}