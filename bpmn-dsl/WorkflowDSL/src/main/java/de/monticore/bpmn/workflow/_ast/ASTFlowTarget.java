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

  public List<ASTFlowNode> asSource() {
    List<ASTFlowNode> sourceFlowNodes = null;
    if (isPresentNodeRef()) {
      sourceFlowNodes = Lists.newArrayList(resolveFlowNode());
    } else if (isPresentNode()) {
      sourceFlowNodes = Lists.newArrayList(getNode());
    } else if (isPresentBlock()) {
      sourceFlowNodes = getBlock().asSource();
    }
    return checkNotNull(sourceFlowNodes);
  }

  public ListMultimap<ASTFlowNode, List<ASTFlowCondition>> asTarget() {
    ListMultimap<ASTFlowNode, List<ASTFlowCondition>> targetFlowNodes = null;
    if (isPresentNodeRef()) {
      targetFlowNodes =
          ImmutableListMultimap.<ASTFlowNode, List<ASTFlowCondition>>builder()
              .put(
                  resolveFlowNode(),
                  isPresentCondition() ? Lists.newArrayList(getCondition()) : Lists.newArrayList())
              .build();
    } else if (isPresentNode()) {
      targetFlowNodes =
          ImmutableListMultimap.<ASTFlowNode, List<ASTFlowCondition>>builder()
              .put(
                  getNode(),
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

  private ASTFlowNode resolveFlowNode() {
    String name = getNodeRef().getQName();
    WorkflowScope scope = (WorkflowScope) getEnclosingScope();

    Optional<ASTFlowNode> flowNode = scope.resolveFlowNodeDown(name);
    if (!flowNode.isPresent()) {
      Log.error(Messages.get("0xWFM1004", name), get_SourcePositionStart());
    }

    return flowNode.get();
  }
}
