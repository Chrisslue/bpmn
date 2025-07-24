/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.collectors.WorkflowLocalCollector;
import java.util.*;
import java.util.stream.Stream;

public class ASTWFSubProcess extends ASTWFSubProcessTOP {
  
  public boolean isAdHoc() { return getType() == ASTConstantsWorkflow.ADHOC; }
  
  public boolean isTransaction() { return getType() == ASTConstantsWorkflow.TRANSACTION; }
  
  public List<ASTWFEvent> getBoundaryEvents() {
    WorkflowLocalCollector<ASTWFEvent> collector = new WorkflowLocalCollector<ASTWFEvent>(this) {
      
      @Override
      public void visit(final ASTWFEvent event) {
        if (event.getSymbol().isBoundary()) {
          select(event);
        }
      }
      
    };
    return collector.collect(collector);
  }
  
  // added additional attributes and methods
  protected List<de.monticore.bpmn.workflow._ast.SequenceFlow> incomings =
      new java.util.ArrayList<>();
  protected List<de.monticore.bpmn.workflow._ast.SequenceFlow> outgoings =
      new java.util.ArrayList<>();
  
  public boolean addIncomings(de.monticore.bpmn.workflow._ast.SequenceFlow element) {
    return this.getIncomingsList().add(element);
  }
  
  public boolean addOutgoings(de.monticore.bpmn.workflow._ast.SequenceFlow element) {
    return this.getOutgoingsList().add(element);
  }
  
  public List<de.monticore.bpmn.workflow._ast.SequenceFlow> getIncomingsList() {
    return this.incomings;
  }
  
  public List<de.monticore.bpmn.workflow._ast.SequenceFlow> getOutgoingsList() {
    return this.outgoings;
  }
  
  public boolean isEmptyIncomings() { return this.getIncomingsList().isEmpty(); }
  
  public boolean isEmptyOutgoings() { return this.getOutgoingsList().isEmpty(); }
  
  public int sizeIncomings() {
    return this.getIncomingsList().size();
  }
  
  public int sizeOutgoings() {
    return this.getOutgoingsList().size();
  }
  
  public Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamOutgoings() {
    return this.getOutgoingsList().stream();
  }
  
  public Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamIncomings() {
    return this.getIncomingsList().stream();
  }
  
}
