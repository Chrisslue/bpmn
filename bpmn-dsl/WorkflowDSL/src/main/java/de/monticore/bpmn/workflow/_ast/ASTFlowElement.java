 /* (c) https://github.com/MontiCore/monticore */ 
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

  // added additional attributes and methods
  /*
  protected  Optional<String> parentRef = Optional.empty();
  protected  Optional<String> laneRef = Optional.empty();
  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> incomings = new java.util.ArrayList<>();
  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> outgoings = new java.util.ArrayList<>();
  */
  abstract  boolean addIncomings (de.monticore.bpmn.workflow._ast.SequenceFlow element);

  abstract  boolean addOutgoings (de.monticore.bpmn.workflow._ast.SequenceFlow element);

  abstract  void setLaneRef (String laneRef);

  abstract  void setParentRef (String parentRef);

  abstract  List<de.monticore.bpmn.workflow._ast.SequenceFlow> getIncomingsList ();

  abstract  List<de.monticore.bpmn.workflow._ast.SequenceFlow> getOutgoingsList ();

  abstract  boolean isEmptyIncomings ();

  abstract  boolean isEmptyOutgoings ();

  abstract  int sizeIncomings ();

  abstract  int sizeOutgoings ();

  abstract  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamOutgoings ();

  abstract  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamIncomings ();
}
