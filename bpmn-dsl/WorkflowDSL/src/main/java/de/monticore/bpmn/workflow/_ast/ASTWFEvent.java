package de.monticore.bpmn.workflow._ast;

import de.se_rwth.commons.logging.Log;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.*;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class ASTWFEvent extends ASTWFEventTOP {

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
    if (super.r__catch) {
      return true;
    }
    else{
    return isStart()
        || getSymbol().isBoundary()
        || (isIntermediate() && new IsIntermediateCatchTrigger().test(this));
    }
  }

  public boolean isThrow() {
    if (r__throw) {
      return true;
    }
    else{
      return isEnd() || (isIntermediate() && new IsIntermediateThrowTrigger().test(this));
    }
  }

  // added additional attributes and methods
  protected  Optional<String> parentRef = Optional.empty();
  protected  Optional<String> laneRef = Optional.empty();
  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> incomings = new java.util.ArrayList<>();
  protected  List<de.monticore.bpmn.workflow._ast.SequenceFlow> outgoings = new java.util.ArrayList<>();

  public  boolean addIncomings (de.monticore.bpmn.workflow._ast.SequenceFlow element) {
    return this.getIncomingsList().add(element);
  }

  public  boolean addOutgoings (de.monticore.bpmn.workflow._ast.SequenceFlow element) {
    return this.getOutgoingsList().add(element);
  }

  public  void setLaneRef (String laneRef) {
    this.laneRef = Optional.ofNullable(laneRef);
  }

  public  void setParentRef (String parentRef) {
    this.parentRef = Optional.ofNullable(parentRef);
  }

  public  List<de.monticore.bpmn.workflow._ast.SequenceFlow> getIncomingsList () {
    return this.incomings;
  }

  public  List<de.monticore.bpmn.workflow._ast.SequenceFlow> getOutgoingsList () {
    return this.outgoings;
  }

  public  boolean isEmptyIncomings () {
    return this.getIncomingsList().isEmpty();
  }

  public  boolean isEmptyOutgoings () {
    return this.getOutgoingsList().isEmpty();
  }

  public  int sizeIncomings () {
    return this.getIncomingsList().size();
  }

  public  int sizeOutgoings () {
    return this.getOutgoingsList().size();
  }

  public  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamOutgoings () {
    return this.getOutgoingsList().stream();
  }

  public  Stream<de.monticore.bpmn.workflow._ast.SequenceFlow> streamIncomings () {
    return this.getIncomingsList().stream();
  }




  class IsIntermediateThrowTrigger implements Predicate<ASTWFEvent>, WorkflowVisitor2 {
    boolean isThrow;

    @Override
    public boolean test(final ASTWFEvent event) {
      if (!event.isPresentTrigger()) {
        return true;
      }
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isThrow;
    }

    @Override
    public void visit(final ASTWFEventTriggerNotification trigger) {
      if(trigger.getType() == ASTConstantsWorkflow.ESCALATE){
        isThrow = true;
      }
      
    }

    @Override
    public void visit(final ASTWFEventTriggerCompensate trigger) {
      isThrow = true;
    }
  }

  class IsIntermediateCatchTrigger implements Predicate<ASTWFEvent>, WorkflowVisitor2 {
    boolean isCatch;

    @Override
    public boolean test(final ASTWFEvent event) {
      /*            if (event.isBoundary()) {
          return true;
      }*/
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isCatch;
    }

    @Override
    public void visit(final ASTWFEventTriggerTimer trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTWFEventTriggerConditional trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTWFEventTriggerMultiple trigger) {
      if (!trigger.isParallelMultiple()) {
        isCatch = true;
      }
    }

  
  }
}
