package de.monticore.bpmn.trafos;

import com.google.common.collect.Lists;
import de.monticore.bpmn.lang.Import;
import de.monticore.bpmn.workflow._ast.WorkflowNodeFactory;
import de.monticore.types.types._ast.ASTImportStatement;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Adds additional imports to a model.
 */
public class AddMoreImports extends WorkflowTransformation {

    private final Collection<Import> imports;

    public AddMoreImports(final Collection<Import> imports) {
        this.imports = imports;
    }

    @Override
    protected void transform() {
        checkNotNull(imports);
        Collection<ASTImportStatement> astImports = imports.stream().map(this::createASTImport).collect(Collectors.toList());

        getAst().addAllImportStatements(astImports);
    }

    private ASTImportStatement createASTImport(final Import imp) {
        return WorkflowNodeFactory.createASTImportStatement(
                Lists.newArrayList(imp.getQualifiedName().split("\\.")), imp.isStar());
    }


}
