/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.stream.Collectors;

public class WorkflowScopesGenitor extends WorkflowScopesGenitorTOP {
  
  @Override
  public IWorkflowArtifactScope createFromAST(ASTWorkflowCompilationUnit rootNode) {
    Log.errorIfNull(rootNode,
        "0xA7004x55587 Error by creating of the WorkflowScopesGenitor symbol table: top ast node is null");
    IWorkflowArtifactScope artifactScope = de.monticore.bpmn.workflow.WorkflowMill.artifactScope();
    final String packageName = rootNode.getPackageName().orElse("");
    artifactScope.setPackageName(packageName);
    List<ASTMCImportStatement> importStatements = rootNode.getMCImportStatementList();
    List<ImportStatement> imports = importStatements.stream().map(
        importStatement -> new ImportStatement(importStatement.getQName(), importStatement
            .isStar())).collect(Collectors.toList());
    artifactScope.setImportsList(imports);
    artifactScope.setAstNode(rootNode);
    putOnStack(artifactScope);
    initArtifactScopeHP1(artifactScope);
    rootNode.accept(getTraverser());
    initArtifactScopeHP2(artifactScope);
    return artifactScope;
  }
  
}
