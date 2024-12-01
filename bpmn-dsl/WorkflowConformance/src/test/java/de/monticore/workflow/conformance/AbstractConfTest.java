package de.monticore.workflow.conformance;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.monticore.bpmn.workflow._symboltable.IWorkflowGlobalScope;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;

public abstract class AbstractConfTest {

  protected static final String MODEL_AUX_DIR = "out/";

  protected static final String MODEL_DIR = "src/test/resources/";

  protected static final String SYMBOL_DIR = "src/test/resources";

  private IWorkflowGlobalScope globalScope;

  public void init() {
    Log.init();
    // Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().clear();
    WorkflowMill.globalScope().setSymbolPath(new MCPath(SYMBOL_DIR));
    BasicSymbolsMill.initializePrimitives();
  }

  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param qualifiedModelName the fully qualified name of the model.
   * @return the root of the parsed model.
   */
  protected ASTWorkflowCompilationUnit parseModel(final String qualifiedModelName) {
    WorkflowParser parser = WorkflowMill.parser();
    Optional<ASTWorkflowCompilationUnit> ast = null;
    try {
      ast =
          parser.parse(
              MODEL_DIR
                  + Names.getPathFromPackage(qualifiedModelName).replaceAll("\\\\", "/")
                  + ".wfm");
    } catch (IOException e) {
      fail("Cannot parse " + qualifiedModelName);
      return null;
    }
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());

    return ast.get();
  }

  protected boolean shouldWriteAuxModels() {
    return false;
  }

  protected WorkflowCoCoChecker getChecker() {
    return WorkflowCoCos.getFullChecker();
  }

  protected ASTWorkflowCompilationUnit loadModel(String qualifiedModelName) {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast =
        tool.parse(
            MODEL_DIR
                + Names.getPathFromPackage(qualifiedModelName).replaceAll("\\\\", "/")
                + ".wfm");

    return checkModel(ast);
  }

  public ASTWorkflowCompilationUnit checkModel(ASTWorkflowCompilationUnit ast) {
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new SequenceFlowNodeReferencesExist());
    checker.checkAll(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddReferenceToParentLane().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    // getChecker().checkAll(ast);

    return ast;
  }

  public ASTWorkflowCompilationUnit parse_str(String input) {
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
