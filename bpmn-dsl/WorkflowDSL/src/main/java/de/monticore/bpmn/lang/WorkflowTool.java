package de.monticore.bpmn.lang;

import com.google.common.collect.Sets;
import de.monticore.ModelingLanguageFamily;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.utils.AuxiliaryModelsWriter;
import de.monticore.bpmn.utils.ModelUtils;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.WorkflowLanguage;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.monticore.bpmn.workflow._symboltable.WorkflowSymbolTable;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.logging.Log;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The main class for working with BPMN models.
 */
public class WorkflowTool {

    private final ModelingLanguageFamily languageFamily = new ModelingLanguageFamily();
    private final WorkflowLanguage workflowLang = new WorkflowLanguage();

    private final Collection<Import> additionalImports = Sets.newHashSet();

    private ModelPath modelPath;
    private ASTWorkflowCompilationUnit ast;

    private WorkflowSymbolTable symTab;

    public WorkflowTool() {
        languageFamily.addModelingLanguage(workflowLang);
        languageFamily.addModelingLanguage(new CD4AnalysisLanguage());
    }

    /**
     * Sets the model path.
     *
     * @param modelPath the model path
     * @return this
     */
    public WorkflowTool setModelPath(final ModelPath modelPath) {
        this.modelPath = modelPath;

        return this;
    }

    /**
     * Adds an import to the model to be loaded.
     *
     * @param imp the import
     * @return this
     */
    public WorkflowTool addImport(final Import imp) {
        checkNotNull(imp);
        additionalImports.add(imp);

        return this;
    }

    /**
     * Loads the model.
     *
     * This method also builds the symbol table and executes default transformations.
     *
     * @param qualifiedModelName the qualified model name
     * @param modelPath the model path
     * @return this
     */
    public WorkflowTool loadModel(final String qualifiedModelName, final ModelPath modelPath) {
        return loadModel(ModelUtils.getCoordinate(qualifiedModelName), modelPath);
    }

    /**
     * Loads the model.
     *
     * This method also builds the symbol table and executes default transformations.
     *
     * @param modelCoordinate the model coordinate
     * @param modelPath the model path
     * @return this
     */
    public WorkflowTool loadModel(final ModelCoordinate modelCoordinate, final ModelPath modelPath) {
        return setModelPath(modelPath)
                .parseModel(modelCoordinate)
                .transform(new AddMoreImports(additionalImports))
                .createSymbolTable()
                .checkCoCos(getPreTrafoCoCos())
                .transform(new AddNameToInlineFlowNodes())
                .transform(new AddSequenceFlowToFlowNodes())
                .transform(new AddReferenceToParentLane())
                .transform(new CreateIOSpecification())
                .transform(new SetSubProcessTriggeredByEvent());
    }

    /**
     * Applies multiple transformations in order on the loaded model.
     *
     * @param trafos the transformations
     * @return this
     */
    public WorkflowTool transform(final List<WorkflowTransformation> trafos) {
        trafos.forEach(this::transform);
        return this;
    }

    /**
     * Applies a transformation on the loaded model.
     *
     * @param trafo the transformation
     * @return this
     */
    public WorkflowTool transform(final WorkflowTransformation trafo) {
        if (null == ast) {
            throw new IllegalStateException();
        }

        trafo.transform(ast);
        return this;
    }

    /**
     * Applies a transformation on the loaded model. Skips the transformation if {@code skip} is {@code true}.
     *
     * This method is mainly for convenience when chaining methods.
     *
     * @param trafo the transformation
     * @param skip indicates if the transformation should be skipped.
     * @return this
     */
    public WorkflowTool transform(final WorkflowTransformation trafo, final boolean skip) {
        return skip ? this : this.transform(trafo);
    }

    /**
     * Checks context conditions on the loaded model.
     *
     * @param checker the context condition checker
     * @return this
     */
    public WorkflowTool checkCoCos(final WorkflowCoCoChecker checker) {
        checkNotNull(checker);
        if (null == ast || null == symTab) {
            throw new IllegalStateException();
        }

        checker.checkAll(ast);

        return this;
    }

    /**
     * Parses the model file.
     *
     * @param modelCoordinate the model coordinate
     * @return this
     */
    private WorkflowTool parseModel(final ModelCoordinate modelCoordinate) {
        checkNotNull(modelCoordinate);
        if (null == modelPath) {
            throw new IllegalStateException();
        }

        String qualifiedModelName = modelCoordinate.getQualifiedBaseName();

        if (!modelCoordinate.hasLocation()) {
            modelPath.resolveModel(modelCoordinate);
        }
        if (!modelCoordinate.exists()) {
            Log.error(Messages.get("0xWFM0002", modelCoordinate.getQualifiedPath()));
            return this;
        }

        Log.info("Start parsing workflow model " + qualifiedModelName + " ...", WorkflowTool.class.getSimpleName());
        Optional<ASTWorkflowCompilationUnit> optAst =
                workflowLang.getModelLoader().loadModel(qualifiedModelName, modelPath);
        if (optAst.isPresent()) {
            setAst(optAst.get());
            Log.info("Finished parsing model " + qualifiedModelName + ". Success!", WorkflowTool.class.getSimpleName());
        } else {
            Log.error(Messages.get("0xWFM0001", qualifiedModelName));
        }

        return this;
    }

    /**
     * Builds the symbol table for the loaded model.
     *
     * @return this
     */
    private WorkflowTool createSymbolTable() {
        if (null == ast) {
            throw new IllegalStateException();
        }

        workflowLang.getSymbolTableCreator(getResolvingConfiguration(), new GlobalScope(modelPath, languageFamily))
                .map(stc -> stc.createFromAST(ast))
                .map(Scope::getSubScopes).map(scopes -> (WorkflowScope) scopes.get(0))
                .map(WorkflowSymbolTable::new)
                .ifPresent(this::setSymTab);

        return this;
    }

    /**
     * Returns the AST of the loaded model.
     *
     * @return the AST
     */
    public ASTWorkflowCompilationUnit getAst() {
        if (null == ast) {
            throw new IllegalStateException();
        }
        return ast;
    }

    private void setAst(final ASTWorkflowCompilationUnit ast) {
        this.ast = ast;
    }

    /**
     * Returns the symbol table of the loaded model.
     *
     * @return the symbol table
     */
    public WorkflowSymbolTable getSymbolTable() {
        if (null == symTab) {
            throw new IllegalStateException();
        }
        return symTab;
    }

    private void setSymTab(final WorkflowSymbolTable symTab) {
        this.symTab = symTab;
    }

    /**
     * Exports the loaded model into the BPMN 2.0 exchange format.
     *
     * @param outputDir the output directory
     * @return this
     */
    public WorkflowTool exportXml(final Path outputDir) {
        checkNotNull(outputDir);
        checkNotNull(ast);
        checkNotNull(symTab);

        try {
            ModelUtils.exportXml(outputDir.toFile(), ast);
        } catch (final JAXBException e) {
            Log.error("Failed to serialize workflow model.", e);
        }

        return this;
    }

    /**
     * Writes auxiliary models and files to disk.
     *
     * @param outputDir the output directory
     * @return this
     * @throws IOException
     */
    public WorkflowTool writeAuxiliaryModels(final Path outputDir) throws IOException {
        checkNotNull(outputDir);
        checkNotNull(ast);
        checkNotNull(symTab);

        new AuxiliaryModelsWriter(ast.getProcess())
                .print(outputDir.resolve(ast.getProcess().getName().toLowerCase()));

        return this;
    }

    private ResolvingConfiguration getResolvingConfiguration() {
        ResolvingConfiguration resolvingConfig = new ResolvingConfiguration();
        resolvingConfig.addDefaultFilters(languageFamily.getAllResolvers());

        return resolvingConfig;
    }

    private WorkflowCoCoChecker getPreTrafoCoCos() {
        return new WorkflowCoCoChecker()
                .addCoCo(new SequenceFlowNodeReferencesExist());
    }

}
