/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.trafos.AddMoreImports;
import de.monticore.bpmn.trafos.AddNameToInlineFlowNodes;
import de.monticore.bpmn.trafos.AddSequenceFlowToFlowNodes;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;

public class BPMNConformanceUtils {
  
  // Add OCL default types, this way we don't need to import them in the models every time
  protected static final ImportStatement OCL_TYPES = new ImportStatement(
      "de.monticore.bpmn._types.ocl.DefaultTypes", true);
  
  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param qualifiedModelName the fully qualified name of the model.
   * @return the root of the parsed model.
   */
  public static ASTWorkflowCompilationUnit loadBPMN(String qualifiedModelName, boolean checkCoCos) {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast = tool.parse(Names.getPathFromPackage(qualifiedModelName)
        .replaceAll("\\\\", "/") + ".wfm");
    completeModel(ast);
    if (checkCoCos) {
      checkCoCos(ast);
    }
    return ast;
  }
  
  private static void completeModel(ASTWorkflowCompilationUnit ast) {
    new AddMoreImports(Lists.newArrayList(OCL_TYPES)).transform(ast);
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    
    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);
  }
  
  public static void checkCoCos(ASTWorkflowCompilationUnit ast) {
    // FullChecker still has some issues
    WorkflowCoCoChecker checker = WorkflowCoCos.getBasicChecker();
    checker.checkAll(ast);
  }
  
  public static ASTWorkflowCompilationUnit parseBPMNString(String input) {
    WorkflowParser parser = new WorkflowParser();
    Optional<ASTWorkflowCompilationUnit> ast = Optional.empty();
    try {
      ast = parser.parse_String(input);
    }
    catch (IOException e) {
      Log.error("Error while parsing workflow", e);
    }
    
    if (ast.isEmpty()) {
      Log.error("Error while parsing workflow");
      assert false;
    }
    completeModel(ast.get());
    return ast.get();
  }
  
}
