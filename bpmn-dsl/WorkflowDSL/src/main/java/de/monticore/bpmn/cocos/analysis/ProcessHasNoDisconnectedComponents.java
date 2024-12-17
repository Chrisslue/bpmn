package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

public class ProcessHasNoDisconnectedComponents extends ProcessGraphCoCo {

  @Override
  public void check(
      final Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph,
      final ASTProcess process) {
    if (process
        .isEmptyFlowElements()) { // allow empty processes (an empty process is disconnected by
      // definition)
      return;
    }
    ConnectivityInspector<ASTFlowElement, EndpointPair<ASTFlowElement>> inspector =
        new ConnectivityInspector<>(processGraph);
    List<Set<ASTFlowElement>> components =
        inspector.connectedSets().stream()
            .filter(
                component -> !isEventSubProcess(component) && !isCompensationActivity(component))
            .collect(Collectors.toList());
    if (components.size() > 1) {
      final String formattedComponents =
          components.stream()
              .map(
                  set ->
                      "{"
                          + set.stream()
                              .map(ASTFlowElement::getName)
                              .sorted()
                              .collect(Collectors.joining(", "))
                          + "}")
              .collect(Collectors.joining(", "));

      Log.warn(Messages.get("0xWFM7010", formattedComponents), process.get_SourcePositionStart());
    }
  }

  private boolean isEventSubProcess(final Set<ASTFlowElement> nodes) {
    if (!nodes.isEmpty()) {
      IsEventSubProcessVisitor visitor = new IsEventSubProcessVisitor();

      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.setWorkflowHandler(visitor);
      nodes.stream().findFirst().get().accept(traverser);
      return visitor.isEventSubProcess();
    }
    return false;
  }

  private boolean isCompensationActivity(final Set<ASTFlowElement> nodes) {
    IsForCompensationVisitor visitor = new IsForCompensationVisitor();

    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.setWorkflowHandler(visitor);
    nodes.stream().findFirst().get().accept(traverser);
    return visitor.isForCompensation();
  }
}
