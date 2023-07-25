package de.monticore.bpmn.wf2lts;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.bpmn.Resources;
import de.monticore.bpmn.wf2lts.collector.StartEventCollector;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphBuildingTraverserTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testNestedGateway() {
    var diagramASt = WF2LTSGenerator.loadBPMN(Resources.NESTED_GATEWAY);
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(1, graph.getGatewayScopes().size());
    var expectedEdges = List.of(
        entry("Start", "GatewaySplit"),
        entry("GatewayMerge", "End")
    );
    Utils.assertSameEdges(graph, expectedEdges);
  }

  @Test
  public void testCyclic() {
    var diagramASt = WF2LTSGenerator.loadBPMN(Resources.CYCLIC);
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(0, Log.getErrorCount());

    var expectedEdges = List.of(
        entry("Start", "A"),
        entry("A", "B"),
        entry("B", "A"),
        entry("A", "End")
    );
    Utils.assertSameEdges(graph, expectedEdges);
  }

  @Test
  public void testCyclicGateway() {
    var diagramASt = WF2LTSGenerator.loadBPMN(Resources.CYCLIC_GATEWAY);
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(1, graph.getGatewayScopes().size());
    assertEquals(0, graph.getGatewayScopes().get(0).getGraph().getGatewayScopes().size());

    var expectedEdges = List.of(
        entry("Start", "A"),
        entry("A", "GatewayMerge"),
        entry("GatewayMerge", "B"),
        entry("B", "GatewaySplit")
    );

    Utils.assertSameEdges(graph, expectedEdges);
  }
}