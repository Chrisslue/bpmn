package de.monticore.bpmn.workflow._symboltable;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._ast.ASTCallableElement;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTWorkflowNode;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class should be refactored once MC 6 is released
 */
public class WorkflowScope extends WorkflowScopeTOP {

    private final static List<SymbolKind> FLOW_NODE_KINDS = Lists.newArrayList(
            SubProcessSymbol.KIND,
            TaskSymbol.KIND,
            CallActivitySymbol.KIND,
            NamedGatewaySymbol.KIND,
            NamedEventSymbol.KIND
    );

    private final static List<SymbolKind> CALLABLE_ELEMENT_KINDS = Lists.newArrayList(
            ProcessSymbol.KIND
    );

    private final static List<SymbolKind> ACTIVITY_KINDS = Lists.newArrayList(
            SubProcessSymbol.KIND,
            TaskSymbol.KIND,
            CallActivitySymbol.KIND
    );

    public WorkflowScope() {
        super();
    }

    public WorkflowScope(final boolean isShadowingScope) {
        super(isShadowingScope);
    }

    public WorkflowScope(final Optional<MutableScope> enclosingScope) {
        super(enclosingScope);
    }

    /*
        MC v5.0.2 does not yet support symbol hierarchies.
        Ideally WorkflowScope#resolve(name, FlowNodeSymbol.KIND) would resolve all concrete types of ASTFlowNode.
     */
    public Optional<ASTFlowNode> resolveFlowNodeDown(final String name) {
        return resolveAstNode(name, FLOW_NODE_KINDS, this::resolveDown)
                .map(ASTFlowNode.class::cast);
    }

    public Optional<ASTCallableElement> resolveCalledElement(final String name) {
        return resolveAstNode(name, CALLABLE_ELEMENT_KINDS, this::resolve)
                .map(ASTCallableElement.class::cast);
    }

    public Optional<ASTActivity> resolveActivityLocally(final String name) {
        return resolveAstNode(name, ACTIVITY_KINDS, this::resolveLocally)
                .map(ASTActivity.class::cast);
    }

    private <U extends Symbol> Optional<ASTNode> resolveAstNode(
            final String name,
            final Collection<SymbolKind> kinds,
            final BiFunction<String, SymbolKind, Optional<U>> resolver
    ) {
        return kinds.stream()
                .map(kind -> resolver.apply(name, kind))
                .filter(Optional::isPresent).map(Optional::get)
                .findFirst()
                .flatMap(Symbol::getAstNode);
    }

}
