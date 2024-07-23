package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.logging.Log;
import java.util.*;

public class BPMNElementStorage {

  private final Map<ASTFlowElement, ASTFlowElement> elementSequence = new HashMap<>();

  private BPMNElementCollector collector;

  public BPMNElementStorage(ASTWorkflowCompilationUnit node) {
    collector = new BPMNElementCollector();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    node.accept(traverser);

    for (ASTSequenceFlow sequenceFlow : collector.getSequenceFlows()) {

      List<ASTFlowTarget> targetList = sequenceFlow.getPathList();
      for (int i = 0; i < targetList.size() - 1; i++) {
        String src = targetList.get(i).getNodeRef().getBaseName();
        String tgt = targetList.get(i + 1).getNodeRef().getBaseName();

        elementSequence.put(collector.getFlowElement(src), collector.getFlowElement(tgt));
      }
    }
  }

  public ASTFlowElement getNext(ASTFlowElement node) {
    if (elementSequence.containsKey(node)) {
      return elementSequence.get(node);
    } else {
      Log.error("Successor of " + node.getClass().getCanonicalName() + " not present");
      return null;
    }
  }

  public ASTFlowElement getElement(String name) {
    return collector.getFlowElement(name);
  }

  public boolean hasNext(ASTFlowElement node) {
    return elementSequence.containsKey(node);
  }
}
