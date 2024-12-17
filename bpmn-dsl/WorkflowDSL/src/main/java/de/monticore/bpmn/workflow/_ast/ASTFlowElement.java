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

  /**
   * Extended FlowElement by a Name a List with all incoming SequenceFlows a List with all outgoing SequenceFlows
   * Methodes need to be checked for correctness!!!
   */

  /*
  List<SequenceFlow> getIncomingsList();

  List<SequenceFlow> getOutgoingsList();

  
  
  
  */
  default String getName(){
    return "";
  }

  default void setLaneRef(String ref){
    
  }

  default Set<ASTFlowElement> getSuccessors() {
    return getOutgoingsList().stream().map(SequenceFlow::getTarget).collect(Collectors.toSet());
  }

  default Set<ASTFlowElement> getPredecessors() {
    return getIncomingsList().stream().map(SequenceFlow::getSource).collect(Collectors.toSet());
  }

  default  List<SequenceFlow> getIncomingsList (){
    return null;
  }

  default  List<SequenceFlow> getOutgoingsList (){
    return null;
  }
  
  default  boolean addOutgoings (de.monticore.bpmn.workflow._ast.SequenceFlow element){
    return getOutgoingsList().add(element);
  }

  default  boolean addIncomings (de.monticore.bpmn.workflow._ast.SequenceFlow element){
    return getIncomingsList().add(element);
  }

  default  boolean isEmptyIncomings (){
    return getIncomingsList().isEmpty();
  }

  default  boolean isEmptyOutgoings (){
    return getOutgoingsList().isEmpty();
  }

  default  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamIncomings (){
    return getIncomingsList().stream();
  }

  default  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamOutgoings (){
    return getOutgoingsList().stream();
  }
  

  /**
   * Determines the flow element container (process or sub-process) that directly contains this
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
