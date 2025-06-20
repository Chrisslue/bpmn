/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._ast;

import java.util.Optional;

public class ASTWorkflowCompilationUnit extends ASTWorkflowCompilationUnitTOP {
  
  public Optional<String> getPackageName() {
    if (isPresentMCPackageDeclaration()) {
      return Optional.of(getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    else {
      return Optional.empty();
    }
  }
  
}
