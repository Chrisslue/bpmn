package de.monticore.wf2lts;

import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import java.util.List;

public class SubProcessScope {

  private final IntermediateGraph internalGraph;

  private final List<ASTEvent> startEvents;

  private final List<ASTEvent> endEvents;

  public SubProcessScope(ASTSubProcess subProcess) {

    // Collect all start events;
    startEvents = StartEventCollector.of(subProcess.getFlowElementList());

    // Collect all end events
    endEvents = EndEventCollector.of(subProcess.getFlowElementList());

    internalGraph = GraphBuildingTraverser.graphOf(startEvents.get(0)); // TODO
  }

  public IntermediateGraph getInternalGraph() {
    return internalGraph;
  }

  public List<ASTEvent> getStartEvents() {
    return startEvents;
  }

  public List<ASTEvent> getEndEvents() {
    return endEvents;
  }
}
