package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.collectors.WorkflowLocalCollector;
import java.util.List;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Stream;

public class ASTSubProcess extends ASTSubProcessTOP {

  public boolean isAdHoc() {
    return getType() == ASTConstantsWorkflow.ADHOC;
  }

  public boolean isTransaction() {
    return getType() == ASTConstantsWorkflow.TRANSACTION;
  }

  public List<ASTEvent> getBoundaryEvents() {
    WorkflowLocalCollector<ASTEvent> collector =
        new WorkflowLocalCollector<ASTEvent>(this) {
          @Override
          public void visit(final ASTEvent event) {
            if (event.getSymbol().isBoundary()) {
              select(event);
            }
          }
        };
    return collector.collect(collector);
  }

  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> incomings = new java.util.ArrayList<>();
  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> outgoings = new java.util.ArrayList<>();

  protected  Optional<String> parentRef = Optional.empty();
  protected  Optional<String> laneRef = Optional.empty();

  public  String getParentRef (){
    if (isPresentParentRef()) {
        return this.parentRef.get();
    }
    Log.error("ParentRef can't return a value. Attribute is empty.");
    throw new IllegalStateException();
  }

  public  boolean isPresentParentRef (){
  return this.parentRef.isPresent();
  }

  public  String getLaneRef (){
    if (isPresentLaneRef()) {
        return this.laneRef.get();
    }
    Log.error("LaneRef can't return a value. Attribute is empty.");
    throw new IllegalStateException();
  }

  public  boolean isPresentLaneRef (){
    return this.laneRef.isPresent();
  }

  public  int sizeIncomings (){
    return this.getIncomingsList().size();
  }

  public  int sizeOutgoings (){
    return this.getOutgoingsList().size();
  }

  public  void setParentRef (String parentRef){
    this.parentRef = Optional.ofNullable(parentRef);
  }

  public  void setLaneRef (String laneRef){
    this.laneRef = Optional.ofNullable(laneRef);
  }

  public  void setIncomingsList (List<de.monticore.bpmn.workflow._ast.SequenceFlow> incomings){
    this.incomings = incomings;
  }

  public  void setOutgoingsList (List<de.monticore.bpmn.workflow._ast.SequenceFlow> outgoings){
    this.outgoings = outgoings;
  }

  public  boolean isEmptyIncomings (){
    return this.getIncomingsList().isEmpty();
  }

  public  boolean isEmptyOutgoings (){
    return this.getOutgoingsList().isEmpty();
  }

  public  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamIncomings (){
    return this.getIncomingsList().stream();
  }

  public  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamOutgoings (){
    return this.getOutgoingsList().stream();
  }

  public  List<SequenceFlow> getIncomingsList (){
    return this.incomings;
  }

  public  List<SequenceFlow> getOutgoingsList (){
    return this.outgoings;
  }

  public  boolean addOutgoings (de.monticore.bpmn.workflow._ast.SequenceFlow element){
    return this.getOutgoingsList().add(element);
  }

  public  boolean addIncomings (de.monticore.bpmn.workflow._ast.SequenceFlow element){
    return this.getIncomingsList().add(element);
  }

}
