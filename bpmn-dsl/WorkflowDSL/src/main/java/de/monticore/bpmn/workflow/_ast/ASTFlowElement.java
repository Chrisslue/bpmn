package de.monticore.bpmn.workflow._ast;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;

import java.util.Optional;

public interface ASTFlowElement extends ASTFlowElementTOP {

    /**
     * Determines the flow element container (process or sub-process) that directly contains this node.
     *
     * @return
     */
    default Optional<ASTFlowElementContainer> getParent() {
        // we can't use a visitor here, as we would need a parent & inheritance-aware visitor
        Optional<? extends Scope> scope = getEnclosingScopeOpt();
        while (scope.isPresent() && !(scope.get() instanceof ArtifactScope)) {
            final Optional<? extends ScopeSpanningSymbol> symbol = scope.get().getSpanningSymbol();
            if (symbol.isPresent()) {
                if (symbol.get().getAstNode().isPresent()) {
                    final ASTNode node = symbol.get().getAstNode().get();
                    if (node instanceof ASTFlowElementContainer) {
                        return Optional.of((ASTFlowElementContainer) node);
                    }
                }
            }
            scope = scope.get().getEnclosingScope();
        }
        return Optional.empty();
    }

}
