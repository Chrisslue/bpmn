package de.monticore.bpmn.wf2lts.scopes;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.wf2lts.Utils;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope.GatewayType;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._ast.ASTInlineGateway;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class GatewayScopeTest {

  @Test
  void testNestedGateway() {

    var ast = WF2LTSGenerator.loadBPMN(resolveDiagram("../NestedGateway"));
    var gateways = collectGateways(ast);
    var outerGateway = gateways.stream()
        .filter(ASTGateway::isDiverging)
        .filter(astGateway -> astGateway.getName().equals("GatewaySplit"))
        .findFirst().orElseThrow();
    var scope = new GatewayScope(WorkflowMill.traverser(), outerGateway);
    assertEquals(GatewayType.PARALLEL, scope.getGatewayType());
    assertTrue(scope.getClosingGateway().isPresent());
    var outerGatewayGraph = scope.getGraph();
    assertEquals(outerGateway, outerGatewayGraph.getStart());
    assertEquals(1, outerGatewayGraph.getGatewayScopes().size());

    var expectedOuterGatewayEdges = List.of(
        entry("GatewaySplit", "A"),
        entry("A", "GatewayMerge"),
        entry("GatewaySplit", "InnerGatewaySplit"),
        entry("InnerGatewayMerge", "E"),
        entry("E", "GatewayMerge")
    );
    Utils.assertSameEdges(outerGatewayGraph, expectedOuterGatewayEdges);

    var innerGatewayScope = outerGatewayGraph.getGatewayScopes().get(0);
    assertEquals(GatewayType.XOR, innerGatewayScope.getGatewayType());
    assertTrue(innerGatewayScope.getClosingGateway().isPresent());
    assertEquals(0, innerGatewayScope.getGraph().getGatewayScopes().size());
    var innerGatewayGraph = innerGatewayScope.getGraph();
    var expectedInnerGatewayEdges = List.of(
        entry("InnerGatewaySplit", "B"),
        entry("B", "InnerGatewayEnd"),
        entry("InnerGatewaySplit", "C"),
        entry("C", "InnerGatewayMerge"),
        entry("InnerGatewaySplit", "D"),
        entry("D", "InnerGatewayMerge"));
    Utils.assertSameEdges(innerGatewayGraph, expectedInnerGatewayEdges);

    var startEdges = innerGatewayGraph.getEdges().get(innerGatewayGraph.getStart());
    assertEquals(3, startEdges.size());
  }

  @Test
  void testCyclicGateway() {
    var ast = WF2LTSGenerator.loadBPMN(resolveDiagram("../CyclicGateway"));
    var gateways = collectGateways(ast);
    var outerGateway = gateways.stream()
        .filter(ASTGateway::isDiverging)
        .filter(astGateway -> astGateway.getName().equals("GatewaySplit"))
        .findFirst().orElseThrow();
    var scope = new GatewayScope(WorkflowMill.traverser(), outerGateway);
    assertEquals(GatewayType.XOR, scope.getGatewayType());
    assertTrue(scope.getClosingGateway().isPresent());

    var expectedEdges = List.of(
        entry("GatewaySplit", "GatewayMerge"),
        entry("GatewaySplit", "C"),
        entry("C", "End")
    );
    var gatewayGraph = scope.getGraph();
    Utils.assertSameEdges(gatewayGraph, expectedEdges);

    assertEquals(outerGateway, gatewayGraph.getStart());
    assertEquals(0, gatewayGraph.getGatewayScopes().size());
  }

  private String resolveDiagram(String diagramName) {
    return Objects.requireNonNull(getClass().getResource(diagramName + ".wfm")).getPath();
  }

  private List<ASTGateway> collectGateways(ASTWorkflowCompilationUnit ast) {
    var visitor = new WorkflowVisitor2() {

      final List<ASTGateway> gateways = new ArrayList<>();

      @Override
      public void visit(ASTNamedGateway node) {
        gateways.add(node);
      }

      @Override
      public void visit(ASTInlineGateway node) {
        gateways.add(node);
      }
    };
    var traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    ast.accept(traverser);
    return visitor.gateways;
  }

}