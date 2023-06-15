package de.monticore.wf2lts.scopes;

import de.monticore.bpmn.workflow._ast.ASTEvent;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.wf2lts.GraphBuildingTraverser;
import de.monticore.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.wf2lts.collector.EndEventCollector;
import de.monticore.wf2lts.collector.StartEventCollector;

import java.util.List;

public class SubProcessScope {

  private final IntermediateGraphWithScopes internalGraph;

  private final List<ASTEvent> startEvents;

  private final List<ASTEvent> endEvents;

  private final ASTSubProcess subProcess;

  public SubProcessScope(ASTSubProcess subProcess) {

    this.subProcess = subProcess;

    // Collect all start events;
    this.startEvents = StartEventCollector.of(subProcess.getFlowElementList());

    // Collect all end events
    this.endEvents = EndEventCollector.of(subProcess.getFlowElementList());

    // TODO handle multiple start events
    this.internalGraph = GraphBuildingTraverser.graphOf(startEvents.get(0));
  }

  public ASTSubProcess getSubProcess() {
    return subProcess;
  }

  public IntermediateGraphWithScopes getInternalGraph() {
    return internalGraph;
  }

  public List<ASTEvent> getStartEvents() {
    return startEvents;
  }

  public List<ASTEvent> getEndEvents() {
    return endEvents;
  }

}
