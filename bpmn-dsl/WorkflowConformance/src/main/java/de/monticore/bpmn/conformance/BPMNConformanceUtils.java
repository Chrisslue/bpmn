package de.monticore.bpmn.conformance;

import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.AddNameToInlineFlowNodes;
import de.monticore.bpmn.trafos.AddSequenceFlowToFlowNodes;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;

public class BPMNConformanceUtils {
  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param qualifiedModelName the fully qualified name of the model.
   * @return the root of the parsed model.
   */
  public static ASTWorkflowCompilationUnit loadBPMN(String qualifiedModelName) {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast =
        tool.parse(Names.getPathFromPackage(qualifiedModelName).replaceAll("\\\\", "/") + ".wfm");

    return checkModel(ast);
  }

  private static ASTWorkflowCompilationUnit checkModel(ASTWorkflowCompilationUnit ast) {
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new SequenceFlowNodeReferencesExist());
    checker.checkAll(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    // getChecker().checkAll(ast);
    return ast;
  }

  public static ASTWorkflowCompilationUnit parseBPMNString(String input) {
    WorkflowParser parser = new WorkflowParser();
    Optional<ASTWorkflowCompilationUnit> ast = Optional.empty();
    try {
      ast = parser.parse_String(input);
    } catch (IOException e) {
      Log.error("Error while parsing workflow", e);
    }

    if (ast.isEmpty()) {
      Log.error("Error while parsing workflow");
      assert false;
    }
    return checkModel(ast.get());
  }
}
