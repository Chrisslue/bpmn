package de.monticore.bpmn.workflow._ast;
import de.se_rwth.commons.logging.Log;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.function.Predicate;
import java.util.*;
import java.util.stream.Stream;

public class ASTEvent extends ASTEventTOP {

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


  public boolean isStart() {
    return getType() == ASTConstantsWorkflow.START;
  }

  public boolean isEnd() {
    return getType() == ASTConstantsWorkflow.END;
  }

  public boolean isIntermediate() {
    return !isStart() && !isEnd();
  }

  public boolean isCatch() {
    if (isCatch()) {
      return true;
    }
    else{
    return isStart()
        || getSymbol().isBoundary()
        || (isIntermediate() && new IsIntermediateCatchTrigger().test(this));
    }
  }

  public boolean isThrow() {
    if (isThrow()) {
      return true;
    }
    else{
      return isEnd() || (isIntermediate() && new IsIntermediateThrowTrigger().test(this));
    }
  }




  class IsIntermediateThrowTrigger implements Predicate<ASTEvent>, WorkflowVisitor2 {
    boolean isThrow;

    @Override
    public boolean test(final ASTEvent event) {
      if (!event.isPresentTrigger()) {
        return true;
      }
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isThrow;
    }

    @Override
    public void visit(final ASTEventTriggerNotification trigger) {
      if(trigger.getType() == ASTConstantsWorkflow.ESCALATE){
        isThrow = true;
      }
      
    }

    @Override
    public void visit(final ASTEventTriggerCompensate trigger) {
      isThrow = true;
    }
  }

  class IsIntermediateCatchTrigger implements Predicate<ASTEvent>, WorkflowVisitor2 {
    boolean isCatch;

    @Override
    public boolean test(final ASTEvent event) {
      /*            if (event.isBoundary()) {
          return true;
      }*/
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isCatch;
    }

    @Override
    public void visit(final ASTEventTriggerTimer trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTEventTriggerConditional trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTEventTriggerMultiple trigger) {
      if (!trigger.isParallelMultiple()) {
        isCatch = true;
      }
    }
  }
}
