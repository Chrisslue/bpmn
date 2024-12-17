package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.ASTActivity;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import java.util.Optional;

/** This class should be refactored once MC 6 is released */
public class WorkflowScope extends WorkflowScopeTOP {

  public WorkflowScope() {
    super();
  }

  public WorkflowScope(final boolean isShadowingScope) {
    super(isShadowingScope);
  }

  /*
     MC v5.0.2 does not yet support symbol hierarchies.
     Ideally WorkflowScope#resolve(name, FlowNodeSymbol.KIND) would resolve all concrete types of ASTFlowElement.
  */
  public Optional<ASTFlowElement> resolveFlowNodeDown(final String name) {
    Optional<SubProcessSymbol> subProcessSymbol = resolveSubProcessDown(name);
    if (subProcessSymbol.isPresent()) {
      if (subProcessSymbol.get().isPresentAstNode()) {
        return Optional.of(subProcessSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    Optional<ActivitySymbol> taskSymbol = resolveActivityDown(name);
    if (taskSymbol.isPresent()) {
      if (taskSymbol.get().isPresentAstNode()) {
        return Optional.of(taskSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    Optional<GatewaySymbol> namedGatewaySymbol = resolveGatewayDown(name);
    if (namedGatewaySymbol.isPresent()) {
      if (namedGatewaySymbol.get().isPresentAstNode()) {
        return Optional.of(namedGatewaySymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    Optional<EventSymbol> namedEventSymbol = resolveEventDown(name);
    if (namedEventSymbol.isPresent()) {
      if (namedEventSymbol.get().isPresentAstNode()) {
        return Optional.of(namedEventSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  public Optional<ASTProcess> resolveCalledElement(final String name) {
    Optional<ProcessSymbol> processSymbol = resolveProcess(name);
    if (processSymbol.isPresent()) {
      if (processSymbol.get().isPresentAstNode()) {
        return Optional.of(processSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }

  /*
  public Optional<ASTActivity> resolveActivityLocally(final String name) {
    Optional<SubProcessSymbol> subProcessSymbol = resolveSubProcessLocally(name);
    if (subProcessSymbol.isPresent()) {
      if (subProcessSymbol.get().isPresentAstNode()) {
        return Optional.of(subProcessSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
  
    Optional<ActivitySymbol> taskSymbol = super.resolveActivityLocally(name);
    if (taskSymbol.isPresent()) {
      if (taskSymbol.get().isPresentAstNode()) {
        return Optional.of(taskSymbol.get().getAstNode());
      } else {
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
  */
}
