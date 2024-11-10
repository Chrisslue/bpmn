package de.monticore.bpmn.workflow._ast;

import de.monticore.ast.ASTNode;
import de.monticore.bpmn.workflow._symboltable.IWorkflowArtifactScope;
import de.monticore.bpmn.workflow._symboltable.IWorkflowScope;
import de.monticore.symboltable.IScopeSpanningSymbol;
import java.util.Optional;

public interface ASTFlowElement extends ASTFlowElementTOP {

  /**
   * Extended FlowElement by 
   * a Name
   * a List with all incoming SequenceFlows
   * a List with all outgoing SequenceFlows 
   */
  String getName();
  List<SequenceFlow> getIncomingsList();
  List<SequenceFlow> getOutgoingsList();

  default Set<ASTFlowNode> getSuccessors() {
    return getOutgoingsList().stream().map(SequenceFlow::getTarget).collect(Collectors.toSet());
  }

  default Set<ASTFlowNode> getPredecessors() {
    return getIncomingsList().stream().map(SequenceFlow::getSource).collect(Collectors.toSet());
  }

  /**
   * Determines the flow element container (process or sub-process) that directly contains this
   * node.
   *
   * @return
   */
  default Optional<ASTFlowElementContainer> getParent() {
    // we can't use a visitor here, as we would need a parent & inheritance-aware visitor
    IWorkflowScope scope = getEnclosingScope();
    while (scope != null && !(scope instanceof IWorkflowArtifactScope)) {
      if (scope.isPresentSpanningSymbol()) {
        final IScopeSpanningSymbol symbol = scope.getSpanningSymbol();
        if (symbol.isPresentAstNode()) {
          final ASTNode node = symbol.getAstNode();
          if (node instanceof ASTFlowElementContainer) {
            return Optional.of((ASTFlowElementContainer) node);
          }
        }
      }
      scope = scope.getEnclosingScope();
    }
    return Optional.empty();
  }
}
