 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.workflow._ast;

import de.se_rwth.commons.logging.Log;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.*;
import java.util.stream.Stream;
import java.util.function.Predicate;
import com.google.common.collect.ListMultimap;

public class ASTSequenceFlow extends ASTSequenceFlowTOP {

  public ListMultimap<ASTFlowElement, List<ASTFlowCondition>> asTarget() {
    return getPath(0).asTarget();
  }

  public Collection<ASTFlowElement> asSource() {
    return getPath(sizePath() - 1).asSource();
  }

  public boolean isDefault() {
    return !isEmptyPath()
        && getPathList().get(0).isPresentCondition()
        && getPathList().get(0).getCondition().isDefault();
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

}