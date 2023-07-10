package de.monticore.bpmn.wf2lts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.wf2lts.collector.StartEventCollector;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
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
    var diagramASt = WF2LTSGenerator.loadBPMN("src/test/resources/de/monticore/wf2lts/NestedGateway.wfm");
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(1, graph.getGatewayScopes().size());
    var gatewayScope = graph.getGatewayScopes().get(0);
    assertEquals(1, gatewayScope.getGraph().getGatewayScopes().size());
    var innerGatewayScope = gatewayScope.getGraph().getGatewayScopes().get(0);
    assertTrue(innerGatewayScope.getClosingGateway().isPresent());
    var innerStart = innerGatewayScope.getGraph().getStart();
    assertEquals("InnerGatewaySplit", innerStart.getName());
    assertEquals(3, innerGatewayScope.getGraph().getEdges().get(innerStart).size());
    assertTrue(gatewayScope.getGraph().getEdges().containsKey(innerGatewayScope.getClosingGateway().get()));
    assertEquals(1, gatewayScope.getGraph().getEdges().get(innerGatewayScope.getClosingGateway().get()).size());
    // Assert most outer graph does not contain nested gateway
    assertFalse(graph.getEdges().containsKey(innerGatewayScope.getClosingGateway().get()));
    assertFalse(graph.getEdges().containsKey(innerGatewayScope.getGraph().getStart()));
  }

  @Test
  public void testCyclic() {
    var diagramASt = WF2LTSGenerator.loadBPMN("src/test/resources/de/monticore/wf2lts/Cyclic.wfm");
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(3, graph.getEdges().keySet().size()); // Start, A, B have outgoing edges
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testCyclicGateway() {
    var diagramASt = WF2LTSGenerator.loadBPMN("src/test/resources/de/monticore/wf2lts/CyclicGateway.wfm");
    var startEvent = StartEventCollector.of(diagramASt.getProcess().getFlowElementList())
        .stream().filter(event -> event.getName().equals("Start")).findFirst().orElseThrow();
    var graph = GraphBuildingTraverser.graphOf(startEvent);
    assertEquals(1, graph.getGatewayScopes().size());
    assertEquals(0, graph.getGatewayScopes().get(0).getGraph().getGatewayScopes().size());
    // TODO Adapt lts logic to handle this case
  }
}