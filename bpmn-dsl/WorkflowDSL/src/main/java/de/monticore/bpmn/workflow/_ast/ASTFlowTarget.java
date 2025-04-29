 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.workflow._ast;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._symboltable.WorkflowScope;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;

public class ASTFlowTarget extends ASTFlowTargetTOP {

  public List<ASTFlowElement> asSource() {
    List<ASTFlowElement> sourceFlowNodes = null;
    if (isPresentElement()) {
      sourceFlowNodes = Lists.newArrayList(resolveFlowNode());
    } else if (isPresentGateway()) {
      sourceFlowNodes = Lists.newArrayList(getGateway());
    } else if (isPresentBlock()) {
      sourceFlowNodes = getBlock().asSource();
    }
    return checkNotNull(sourceFlowNodes);
  }

  public ListMultimap<ASTFlowElement, List<ASTFlowCondition>> asTarget() {
    ListMultimap<ASTFlowElement, List<ASTFlowCondition>> targetFlowNodes = null;
    if (isPresentElement()) {
      targetFlowNodes =
          ImmutableListMultimap.<ASTFlowElement, List<ASTFlowCondition>>builder()
              .put(
                  resolveFlowNode(),
                  isPresentCondition() ? Lists.newArrayList(getCondition()) : Lists.newArrayList())
              .build();
    } else if (isPresentGateway()) {
      targetFlowNodes =
          ImmutableListMultimap.<ASTFlowElement, List<ASTFlowCondition>>builder()
              .put(
                  getGateway(),
                  isPresentCondition() ? Lists.newArrayList(getCondition()) : Lists.newArrayList())
              .build();
    } else if (isPresentBlock()) {
      targetFlowNodes = getBlock().asTarget();
      if (isPresentCondition()) {
        targetFlowNodes.values().forEach(conditions -> conditions.add(0, getCondition()));
      }
    }
    return checkNotNull(targetFlowNodes);
  }

  private ASTFlowElement resolveFlowNode() {
    String name = getElement().getQName();
    WorkflowScope scope = (WorkflowScope) getEnclosingScope();

    Optional<ASTFlowElement> flowNode = scope.resolveFlowNodeDown(name);
    if (!flowNode.isPresent()) {
      Log.error(Messages.get("0xWFM1004", name), get_SourcePositionStart());
    }

    return flowNode.get();
  }
}
