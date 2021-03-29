package de.monticore.bpmn.workflow._ast;

import de.monticore.types.types._ast.ASTImportStatement;

import java.util.List;
import java.util.Optional;

public class ASTWorkflowCompilationUnit extends ASTWorkflowCompilationUnitTOP {

    protected ASTWorkflowCompilationUnit() {
        super();
    }

    protected ASTWorkflowCompilationUnit(
            final Optional<ASTPackageStatement> packageStatement,
            final List<ASTImportStatement> importStatements,
            final ASTProcess process
    ) {
        super(packageStatement, importStatements, process);
    }

    public Optional<String> getPackageName() {
        if (isPresentPackageStatement()) {
            return Optional.of(getPackageStatement().getQualifiedPackage());
        } else {
            return Optional.empty();
        }
    }

}
