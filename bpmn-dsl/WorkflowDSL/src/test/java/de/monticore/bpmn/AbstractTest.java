 /* (c) https://github.com/MontiCore/monticore */ 
/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn;

import static de.se_rwth.commons.Names.getPathFromQualifiedName;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.utils.AuxiliaryModelsWriter;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.monticore.bpmn.workflow._symboltable.IWorkflowGlobalScope;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.io.paths.MCPath;
//import de.monticore.ocl.ocl.types3.OCLTypeCheck3;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/** Abstract test with default methods for loading models. */
public abstract class AbstractTest {

  protected static final String MODEL_AUX_DIR = "out/";

  protected static final String MODEL_DIR = "src/test/resources/";

  protected static final String SYMBOL_DIR = "src/test/resources";

  // Add OCL default types, this way we don't need to import them in the models every time
  protected static final ImportStatement OCL_TYPES =
      new ImportStatement("de.monticore.bpmn._types.ocl.DefaultTypes", true);

  private IWorkflowGlobalScope globalScope;

  @BeforeAll
  public static void init() {
    Log.init();
    Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().clear();
    WorkflowMill.globalScope().setSymbolPath(new MCPath(SYMBOL_DIR));
    BasicSymbolsMill.initializePrimitives();
    //OCLTypeCheck3.init();
  }

  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
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

  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param qualifiedModelName the fully qualified name of the model.
   * @return the root of the parsed model.
   */
  protected ASTWorkflowCompilationUnit loadModel(final String qualifiedModelName) {
    ASTWorkflowCompilationUnit ast = parseModel(qualifiedModelName);
    new AddMoreImports(Lists.newArrayList(OCL_TYPES)).transform(ast);
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

    if (shouldWriteAuxModels()) {
      writeTestAuxModels(qualifiedModelName, ast);
    }

    return ast;
  }

  protected boolean shouldWriteAuxModels() {
    return false;
  }

  protected void writeTestAuxModels(
      final String qualifiedModelName, final ASTWorkflowCompilationUnit unit) {
    Path out =
        get(MODEL_AUX_DIR)
            .resolve(get(getPathFromQualifiedName(qualifiedModelName)))
            .resolve(getSimpleName(qualifiedModelName).toLowerCase());
    try {
      new AuxiliaryModelsWriter(unit.getWFProcess()).print(out);
    } catch (IOException ignored) {
    }
  }
}
