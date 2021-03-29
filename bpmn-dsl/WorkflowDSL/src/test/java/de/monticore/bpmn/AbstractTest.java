/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn;

import de.monticore.bpmn.lang.Import;
import de.monticore.bpmn.lang.WorkflowTool;
import de.monticore.bpmn.utils.AuxiliaryModelsWriter;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Path;

import static de.se_rwth.commons.Names.getPathFromQualifiedName;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;

/**
 * Abstract test with default methods for loading models.
 */
abstract public class AbstractTest {

    protected final static String MODEL_AUX_DIR = "out/";

    protected final static String MODEL_DIR = "src/test/resources/";

    // Add OCL default types, this way we don't need to import them in the models every time
    protected static final Import OCL_TYPES = new Import("de.monticore.bpmn._types.ocl.DefaultTypes", true);

    private GlobalScope globalScope;

    @BeforeAll
    public static void init() {
        // LogStub.init();
        Log.enableFailQuick(false);
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
    protected ASTWorkflowCompilationUnit loadModel(final String qualifiedModelName) {
        ModelPath modelPath = new ModelPath(get(MODEL_DIR));

        ASTWorkflowCompilationUnit unit = new WorkflowTool()
                .addImport(OCL_TYPES)
                .loadModel(qualifiedModelName, modelPath)
                .getAst();

        if (shouldWriteAuxModels()) {
            writeTestAuxModels(qualifiedModelName, unit);
        }

        return unit;
    }

    protected boolean shouldWriteAuxModels() {
        return false;
    }

    protected void writeTestAuxModels(final String qualifiedModelName, final ASTWorkflowCompilationUnit unit) {
        Path out = get(MODEL_AUX_DIR)
                .resolve(get(getPathFromQualifiedName(qualifiedModelName)))
                .resolve(getSimpleName(qualifiedModelName).toLowerCase());
        try {
            new AuxiliaryModelsWriter(unit.getProcess()).print(out);
        } catch (IOException ignored) {
        }
    }

}
