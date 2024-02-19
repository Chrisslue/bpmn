package de.monticore.bpmn.cocos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.Lists;
import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.Assert;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Context condition test with methods for comparing actual and expected error messages and
 * warnings.
 */
public abstract class AbstractCoCoTest extends AbstractTest {

  /**
   * Returns the context condition checker to be executed by this test.
   *
   * @return the context condition checker.
   */
  protected abstract WorkflowCoCoChecker getChecker();

  /**
   * Asserts that each of the expectedErrors is found (checking code and msg) in any of the actual
   * produced errors that occurred when the {@link WorkflowCoCoChecker} run on the given modelName.
   * Furthermore, it is asserted that there are not any other errors.
   *
   * @param qualifiedModelName full qualified model path
   * @param expectedErrors Collection of the expected errors
   * @return the compilation unit loaded from the model
   */
  protected ASTWorkflowCompilationUnit testModelForErrors(
      String qualifiedModelName, Collection<Finding> expectedErrors) {
    ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);

 //   Assert.assertEqualErrorCounts(expectedErrors, errors);
    Assert.assertHasErrorCodes(expectedErrors);

    return cu;
  }

  protected ASTWorkflowCompilationUnit testModelForErrors(
      String qualifiedModelName,
      Collection<Finding> expectedErrors,
      Collection<Finding> expectedWarnings) {
    ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);

    Collection<Finding> errors =
        Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
    Assert.assertEqualErrorCounts(expectedErrors, errors);
    Assert.assertErrorMsg(expectedErrors, errors);

    Collection<Finding> warnings =
        Log.getFindings().stream().filter(Finding::isWarning).collect(Collectors.toList());
    Assert.assertEqualErrorCounts(expectedWarnings, warnings);
    Assert.assertErrorMsg(expectedWarnings, warnings);

    return cu;
  }

  protected void testModelForErrors(
          String qualifiedModelName, Collection<Finding> expectedErrors, Class exception) {
    assertThrows(exception, () -> {
      loadModel(qualifiedModelName);
    });

    Collection<Finding> errors =
            Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
    Assert.assertEqualErrorCounts(expectedErrors, errors);
    Assert.assertErrorMsg(expectedErrors, errors);
  }

  /**
   * Asserts that no error occurred when the {@link WorkflowCoCoChecker} run the given modelName.
   *
   * @param qualifiedModelName full qualified model path
   * @return the compilation unit loaded from the model
   */
  protected ASTWorkflowCompilationUnit testModelNoErrors(String qualifiedModelName) {
    ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);
    assertEquals(0, Log.getFindings().stream().filter(Finding::isError).count());
    assertEquals(0, Log.getFindings().stream().filter(Finding::isWarning).count());

    return cu;
  }

  protected ASTWorkflowCompilationUnit testModelNoErrors(String qualifiedModelName, int noOfWarnings) {
    ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);
    assertEquals(0, Log.getFindings().stream().filter(Finding::isError).count());
    assertEquals(noOfWarnings, Log.getFindings().stream().filter(Finding::isWarning).count());

    return cu;
  }

  @Override
  protected ASTWorkflowCompilationUnit loadModel(String qualifiedModelName) {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast =
        tool.parse(
            MODEL_DIR
                + Names.getPathFromPackage(qualifiedModelName).replaceAll("\\\\", "/")
                + ".wfm");
    new AddMoreImports(Lists.newArrayList(OCL_TYPES)).transform(ast);
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new SequenceFlowNodeReferencesExist());
    checker.checkAll(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddReferenceToParentLane().transform(ast);
    new CreateIOSpecification().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    if (shouldWriteAuxModels()) { // write models before running CoCos (and potentially failing)
      writeTestAuxModels(qualifiedModelName, ast);
    }
    getChecker().checkAll(ast);

    return ast;
  }
}
