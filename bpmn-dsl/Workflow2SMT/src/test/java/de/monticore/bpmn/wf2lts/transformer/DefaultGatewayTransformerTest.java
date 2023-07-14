package de.monticore.bpmn.wf2lts.transformer;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.bpmn.wf2lts.DefaultNamingStrategy;
import de.monticore.bpmn.wf2lts.DoNothingInterleaving;
import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope.GatewayType;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTGatewayTypeBuilder;
import de.monticore.bpmn.workflow._ast.ASTInlineGateway;
import de.monticore.bpmn.workflow._ast.ASTNamedGateway;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DefaultGatewayTransformerTest {

  private GatewayScope buildGatewayScope(String splitName, String mergingName) {
    var graph = setupGraphWithScope("MultipleIncomingOutgoing", GatewayType.XOR);
    var scope = graph.getGatewayScopes().get(0);
    if (!scope.getGraph().getStart().getName().equals(splitName)) {
      Assertions.fail("Expected " + splitName + " as name of split-gateway");
    }
    if (!scope.getClosingGateway().orElseThrow().getName().equals(mergingName)) {
      Assertions.fail("Expected " + mergingName + " as name of merge-gateway");
    }
    return scope;
  }

  @Test
  void testMultipleIncomingAndOutgoing() {
    /*
     * Start -> @Pre0 -> Split;
     * Start -> @Pre1 -> Split;
     * Start -> @Pre2 -> Split;
     * Merge -> @Next0;
     * Merge -> @Next1;
     * Merge -> @Next2;
     */
    LTS externalLTS = new LTS();
    var gatewaySplitSource = new State();
    var gatewayMergeTarget = new State();
    var splitName = "Split";
    var mergingName = "Merge";
    var preName = "@Pre";
    var postName = "@Post";
    for (int i = 0; i < 3; i++) {
      var preState = new State();
      externalLTS.addTransition(new Transition(externalLTS.getStart(), emptyList(), "Start", preState));
      externalLTS.addTransition(new Transition(preState, emptyList(), preName + i, gatewaySplitSource));

      var postState = new State();
      externalLTS.addTransition(new Transition(gatewayMergeTarget, emptyList(), postName + i, postState));
      externalLTS.addTransition(new Transition(postState, emptyList(), "End", new State()));
    }

    externalLTS.addTransition(new Transition(gatewaySplitSource, emptyList(), splitName, new State()));
    externalLTS.addTransition(new Transition(new State(), emptyList(), mergingName, gatewayMergeTarget));

    var namingStrategy = new DefaultNamingStrategy();
    // Finished initialization.

    // Convert gateway.
    var lts = new DefaultGatewayTransformer(new DoNothingInterleaving(), namingStrategy)
        .transform(
            buildGatewayScope(splitName, mergingName),
            externalLTS,
            new DefaultGraph2LTSTransformer());

    // Test possible paths with pre and post.
    for (var preIdx : List.of(0, 1, 2)) {
      for (var internal : List.of("A", "B")) {
        for (var postIdx : List.of(0, 1, 2)) {
          LTSTestingUtils.assertPathExists(lts,
              List.of("Start", preName + preIdx, internal, postName + postIdx, "End"));
        }
      }
    }

    // Assert that after the unique "C" transition only "Term" is possible.
    var cTransitions = lts.getTransitionsForLabel("C");
    assertEquals(1, cTransitions.size());
    var successor = lts.getOutgoings(cTransitions.get(0).getTarget());
    assertEquals(1, successor.size());
    assertEquals("Term", successor.get(0).getLabel());

    // Assert that there are no new "End" transitions.
    assertEquals(3, lts.getTransitionsForLabel("End").size());

    // Assert no unreachable states.
    var allStates = lts.getStates();
    new LTSTraverser(lts).depthFirstSearchLTS(lts.getStart(), allStates::remove);
    Assertions.assertTrue(allStates.isEmpty(), "LTS has unreachable states");
  }

  public static Stream<Arguments> methodArgumentProvider() {
    var gatewayTypes = List.of(GatewayType.XOR, GatewayType.IOR, GatewayType.PARALLEL);
    Stream<Arguments> withMergingGateway = gatewayTypes.stream()
        .map(type -> Arguments.of(
            "WithMergingGateway",
            type,
            pathsForWithMerging(type)));
    Stream<Arguments> noMergingGateway = gatewayTypes.stream()
        .map(type -> Arguments.of(
            "NoMergingGateway",
            type,
            pathsForNoMerging(type)));
    Stream<Arguments> mergingAndEndGateway = gatewayTypes.stream()
        .map(type -> Arguments.of(
            "MergingAndEndGateway",
            type,
            pathsForMergingAndEnd(type)));

    return Stream.concat(withMergingGateway, Stream.concat(noMergingGateway, mergingAndEndGateway));


  }

  private static List<List<String>> pathsForWithMerging(GatewayType gatewayType) {
    if (gatewayType == GatewayType.XOR) {
      return List.of(
          List.of("Start", "A", "B", "D", "End"),
          List.of("Start", "A", "C", "D", "End"));
    }
    if (gatewayType == GatewayType.IOR) {
      return List.of(
          List.of("Start", "A", "B", "D", "End"),
          List.of("Start", "A", "B", "C", "D", "End"),
          List.of("Start", "A", "C", "D", "End"),
          List.of("Start", "A", "C", "B", "D", "End")
      );
    }
    if (gatewayType == GatewayType.PARALLEL) {
      return List.of(
          List.of("Start", "A", "B", "C", "D", "End"),
          List.of("Start", "A", "C", "B", "D", "End"));
    }
    throw new IllegalArgumentException("No paths defined for gatewayType: " + gatewayType);
  }

  private static List<List<String>> pathsForNoMerging(GatewayType gatewayType) {
    if (gatewayType == GatewayType.XOR) {
      return List.of(
          List.of("Start", "A", "B", "C", "End"),
          List.of("Start", "A", "D", "Term"));
    }
    if (gatewayType == GatewayType.IOR) {
      return List.of(
          List.of("Start", "A", "B", "C", "End"),
          List.of("Start", "A", "D", "Term")
      );
    }
    if (gatewayType == GatewayType.PARALLEL) {
      return LTSTestingUtils.generatePermutations(List.of("B", "C", "D", "End", "Term")).stream()
          .filter(path -> LTSTestingUtils.xComesBeforeY(path, "B", "C"))
          .filter(path -> LTSTestingUtils.xComesBeforeY(path, "C", "End"))
          .filter(path -> LTSTestingUtils.xComesBeforeY(path, "D", "Term"))
          .map(path -> Stream.concat(Stream.of("Start", "A"), path.stream()))
          .map(pathStream -> pathStream.collect(Collectors.toList()))
          .collect(Collectors.toList());
    }
    throw new IllegalArgumentException("No paths defined for gatewayType: " + gatewayType);
  }

  private static List<List<String>> pathsForMergingAndEnd(GatewayType gatewayType) {
    if (gatewayType == GatewayType.XOR) {
      return List.of(
          List.of("Start", "A", "B", "E", "End"),
          List.of("Start", "A", "C", "E", "End"),
          List.of("Start", "A", "D", "Term"));
    }
    if (gatewayType == GatewayType.IOR) {
      return List.of(
          List.of("Start", "A", "B", "E", "End"),
          List.of("Start", "A", "C", "E", "End"),
          List.of("Start", "A", "D", "Term"),
          List.of("Start", "A", "B", "C", "D", "Term"),
          List.of("Start", "A", "C", "B", "D", "Term"),
          List.of("Start", "A", "B", "D", "Term"),
          List.of("Start", "A", "C", "D", "Term"),
          List.of("Start", "A", "B", "C", "E", "End"),
          List.of("Start", "A", "C", "B", "E", "End")
      );
    }
    if (gatewayType == GatewayType.PARALLEL) {
      return LTSTestingUtils.generatePermutations(List.of("B", "C", "D", "Term")).stream()
          .filter(path -> LTSTestingUtils.xComesBeforeY(path, "D", "Term"))
          .map(path -> Stream.concat(Stream.of("Start", "A"), path.stream()).collect(Collectors.toList()))
          .map(path -> path.get(path.size() - 1).equals("Term") ? path :
              Stream.concat(path.stream(), Stream.of("E", "End")).collect(Collectors.toList()))
          .collect(Collectors.toList());
    }
    throw new IllegalArgumentException("No paths defined for gatewayType: " + gatewayType);
  }

  @ParameterizedTest
  @MethodSource("methodArgumentProvider")
  void testDiagram(String diagramName, GatewayType type, List<List<String>> possiblePaths) {
    var graphWithScope = setupGraphWithScope(diagramName, type);

    LTS lts = new DefaultGraph2LTSTransformer().transform(graphWithScope);

    var ltsTraverser = new LTSTraverser(lts);

    var allStates = lts.getStates();
    ltsTraverser.depthFirstSearchLTS(lts.getStart(), allStates::remove);
    Assertions.assertTrue(allStates.isEmpty(), "LTS has unreachable states");

    LTSTestingUtils.assertPathsExist(lts, possiblePaths);
  }

  private IntermediateGraphWithScopes setupGraphWithScope(String diagramName, GatewayType type) {
    var testDiagram = Objects.requireNonNull(getClass()
        .getResource(diagramName + ".wfm")).getPath();
    var ast = WF2LTSGenerator.loadBPMN(testDiagram);
    var graph = WF2LTSGenerator.transformToGraph(setGatewayType(ast, type));
    assertEquals(1, graph.getGatewayScopes().size());
    return graph;
  }

  private ASTWorkflowCompilationUnit setGatewayType(ASTWorkflowCompilationUnit ast, GatewayType type) {
    var astGatewayTypeBuilder = new ASTGatewayTypeBuilder();
    switch (type) {

      case XOR:
        astGatewayTypeBuilder.setExclusive(true);
        break;
      case IOR:
        astGatewayTypeBuilder.setInclusive(true);
        break;
      case PARALLEL:
        astGatewayTypeBuilder.setParallel(true);
        break;
      case EVENT_PARALLEL:
        astGatewayTypeBuilder.setParallelEventBased(true);
        break;
      case EVENT_XOR:
        astGatewayTypeBuilder.setExclusiveEventBased(true);
        break;
      case COMPLEX:
        astGatewayTypeBuilder.setComplex(true);
        break;
    }
    var astGatewayType = astGatewayTypeBuilder.build();

    var astTraverser = WorkflowMill.traverser();
    astTraverser.add4Workflow(new WorkflowVisitor2() {
      @Override
      public void visit(ASTNamedGateway gateway) {
        gateway.setType(astGatewayType);
      }

      @Override
      public void visit(ASTInlineGateway gateway) {
        gateway.setType(astGatewayType);
      }
    });
    ast.accept(astTraverser);
    return ast;
  }

}
