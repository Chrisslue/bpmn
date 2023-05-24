package de.monticore.bpmn.trafos;

import static com.google.common.base.Preconditions.checkNotNull;

import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatementBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/** Adds additional imports to a model. */
public class AddMoreImports extends WorkflowTransformation {

  private final Collection<ImportStatement> imports;

  public AddMoreImports(final Collection<ImportStatement> imports) {
    this.imports = imports;
  }

  @Override
  protected void transform() {
    checkNotNull(imports);
    Collection<ASTMCImportStatement> astImports =
        imports.stream().map(this::createASTImport).collect(Collectors.toList());

    getAst().addAllMCImportStatements(astImports);
  }

  private ASTMCImportStatement createASTImport(final ImportStatement imp) {
    ASTMCQualifiedName qualName =
        new ASTMCQualifiedNameBuilder()
            .setPartsList(
                Arrays.stream(imp.getStatement().split("\\.")).collect(Collectors.toList()))
            .build();
    return new ASTMCImportStatementBuilder()
        .setMCQualifiedName(qualName)
        .setStar(imp.isStar())
        .build();
  }
}
