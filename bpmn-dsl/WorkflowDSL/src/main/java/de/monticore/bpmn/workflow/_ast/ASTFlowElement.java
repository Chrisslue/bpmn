package de.monticore.bpmn.workflow._ast;

import de.monticore.ast.ASTNode;
import de.monticore.bpmn.workflow._symboltable.IWorkflowArtifactScope;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ASTFlowElement extends ASTFlowElementTOP {

  default String getName(){
    return "";
  }

  default Set<ASTFlowElement> getSuccessors() {
    return getOutgoingsList().stream().map(SequenceFlow::getTarget).collect(Collectors.toSet());
  }

  default Set<ASTFlowElement> getPredecessors() {
    return getIncomingsList().stream().map(SequenceFlow::getSource).collect(Collectors.toSet());
  }
  
  /**
   * Determines the flow element container (process xor sub-process) that directly contains this
   * node.
   *
   * @return
   */
  default Optional<ASTFlowElement> getParent() {
    // we can't use a visitor here, as we would need a parent & inheritance-aware visitor
    IWorkflowScope scope = getEnclosingScope();
    while (scope != null && !(scope instanceof IWorkflowArtifactScope)) {
      if (scope.isPresentSpanningSymbol()) {
        final IScopeSpanningSymbol symbol = scope.getSpanningSymbol();
        if (symbol.isPresentAstNode()) {
          final ASTNode node = symbol.getAstNode();
          if (node instanceof ASTFlowElement) {
            return Optional.of((ASTFlowElement) node);
          }
        }
      }
      scope = scope.getEnclosingScope();
    }
    return Optional.empty();
  }
}
