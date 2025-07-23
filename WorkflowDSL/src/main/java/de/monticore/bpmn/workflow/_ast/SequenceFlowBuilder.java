/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;

public class SequenceFlowBuilder {
  
  private ASTFlowElement source;
  private ASTFlowElement target;
  private List<ASTFlowCondition> conditions = Lists.newArrayList();
  
  public SequenceFlowBuilder() {}
  
  public SequenceFlowBuilder setSource(final ASTFlowElement source) {
    this.source = source;
    return this;
  }
  
  public SequenceFlowBuilder setTarget(final ASTFlowElement target) {
    this.target = target;
    return this;
  }
  
  public SequenceFlowBuilder addCondition(final ASTFlowCondition condition) {
    conditions.add(condition);
    return this;
  }
  
  public SequenceFlowBuilder addCondition(final Optional<ASTFlowCondition> condition) {
    condition.ifPresent(conditions::add);
    return this;
  }
  
  public SequenceFlowBuilder addConditions(final List<ASTFlowCondition> conditions) {
    this.conditions.addAll(conditions);
    return this;
  }
  
  public SequenceFlow build() {
    return new SequenceFlow(source, target, conditions);
  }
  
}
