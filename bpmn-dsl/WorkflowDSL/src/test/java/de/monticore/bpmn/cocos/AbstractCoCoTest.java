package de.monticore.bpmn.cocos;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.Assert;
import de.monticore.bpmn.lang.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Context condition test with methods for comparing actual and expected error messages and warnings.
 */
public abstract class AbstractCoCoTest extends AbstractTest {

    /**
     * Returns the context condition checker to be executed by this test.
     *
     * @return the context condition checker.
     */
    abstract protected WorkflowCoCoChecker getChecker();

    /**
     * Asserts that each of the expectedErrors is found (checking code and msg) in any of the actual produced errors
     * that occurred when the {@link WorkflowCoCoChecker} run on the given modelName.
     * Furthermore, it is asserted that there are not any other errors.
     *
     * @param qualifiedModelName full qualified model path
     * @param expectedErrors     Collection of the expected errors
     * @return the compilation unit loaded from the model
     */
    protected ASTWorkflowCompilationUnit testModelForErrors(String qualifiedModelName, Collection<Finding> expectedErrors) {
        ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);

        Collection<Finding> errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
        Assert.assertEqualErrorCounts(expectedErrors, errors);
        Assert.assertErrorMsg(expectedErrors, errors);

        return cu;
    }

    protected ASTWorkflowCompilationUnit testModelForErrors(
            String qualifiedModelName,
            Collection<Finding> expectedErrors,
            Collection<Finding> expectedWarnings
    ) {
        ASTWorkflowCompilationUnit cu = loadModel(qualifiedModelName);

        Collection<Finding> errors = Log.getFindings().stream().filter(Finding::isError).collect(Collectors.toList());
        Assert.assertEqualErrorCounts(expectedErrors, errors);
        Assert.assertErrorMsg(expectedErrors, errors);

        Collection<Finding> warnings = Log.getFindings().stream().filter(Finding::isWarning).collect(Collectors.toList());
        Assert.assertEqualErrorCounts(expectedWarnings, warnings);
        Assert.assertErrorMsg(expectedWarnings, warnings);

        return cu;
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

    @Override
    protected ASTWorkflowCompilationUnit loadModel(String qualifiedModelName) {
        ModelPath modelPath = new ModelPath(Paths.get(MODEL_DIR));

        WorkflowTool tool = new WorkflowTool()
                .addImport(OCL_TYPES)
                .loadModel(qualifiedModelName, modelPath);

        if (shouldWriteAuxModels()) { // write models before running CoCos (and potentially failing)
            writeTestAuxModels(qualifiedModelName, tool.getAst());
        }
        tool.checkCoCos(getChecker());

        return tool.getAst();
    }

}
