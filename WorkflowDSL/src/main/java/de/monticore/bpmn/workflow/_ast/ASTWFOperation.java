/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._ast;

import java.util.stream.Stream;
import java.util.*;

public class ASTWFOperation extends ASTWFOperationTOP {
  
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
