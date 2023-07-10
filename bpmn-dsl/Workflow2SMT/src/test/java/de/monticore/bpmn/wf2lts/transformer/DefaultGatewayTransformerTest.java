package de.monticore.bpmn.wf2lts.transformer;

import static de.monticore.bpmn.wf2lts.Utils.equalIgnoreOrder;
import static java.util.Collections.emptyList;

import de.monticore.bpmn.wf2lts.DefaultNamingStrategy;
import de.monticore.bpmn.wf2lts.DoNothingInterleaving;
import de.monticore.bpmn.wf2lts.GraphBuildingTraverser;
import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTEventType;
import de.monticore.bpmn.workflow._ast.ASTGatewayDirection;
import de.monticore.bpmn.workflow._ast.ASTGatewayTypeBuilder;
import de.monticore.bpmn.workflow._ast.ASTNamedEventBuilder;
import de.monticore.bpmn.workflow._ast.ASTNamedGatewayBuilder;
import de.monticore.bpmn.workflow._ast.ASTTaskBuilder;
import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.bpmn.workflow._ast.SequenceFlowBuilder;
import de.monticore.lts.LTS2Mermaid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultGatewayTransformerTest {

  private GatewayScope buildGatewayScope(String splitName, String mergingName) {

    var taskA = new ASTTaskBuilder().setName("A").build();
    var taskB = new ASTTaskBuilder().setName("B").build();
    var taskC = new ASTTaskBuilder().setName("C").build();
    var endEvent = new ASTNamedEventBuilder().setName("End").setType(ASTEventType.END).build();
    var mergingGateway =
        new ASTNamedGatewayBuilder()
            .setName(mergingName)
            .setDirection(ASTGatewayDirection.MERGE)
            .setType(new ASTGatewayTypeBuilder().setParallel(true).build())
            .build();
    var splittingGateway =
        new ASTNamedGatewayBuilder()
            .setName(splitName)
            .setDirection(ASTGatewayDirection.SPLIT)
            .setType(new ASTGatewayTypeBuilder().setParallel(true).build())
            .build();

    var c2End = new SequenceFlowBuilder().setSource(taskC).setTarget(endEvent).build();
    endEvent.setIncomingsList(List.of(c2End));
    taskC.setOutgoingsList(List.of(c2End));

    for (var task : List.of(taskA, taskB, taskC)) {
      var flow = new SequenceFlowBuilder().setSource(splittingGateway).setTarget(task).build();
      splittingGateway.getOutgoingsList().add(flow);
      task.getIncomingsList().add(flow);
    }
    for (var task : List.of(taskA, taskB)) {
      var flow = new SequenceFlowBuilder().setSource(task).setTarget(mergingGateway).build();
      task.getOutgoingsList().add(flow);
      mergingGateway.getIncomingsList().add(flow);
    }
    return new GatewayScope(WorkflowMill.traverser(), splittingGateway);
  }

  private LTS internalGraph(String splitName, String mergingName) {
    LTS internalGraph = new LTS();
    var splitState = new LTS.State();
    var mergeState = new LTS.State();
    var endState = new LTS.State();
    var aState = new LTS.State();
    var bState = new LTS.State();
    var cState = new LTS.State();
    internalGraph.addTransition(
        new Transition(internalGraph.getStart(), emptyList(), splitName, splitState));
    internalGraph.addTransition(new Transition(splitState, emptyList(), "A", aState));
    internalGraph.addTransition(new Transition(splitState, emptyList(), "B", bState));
    internalGraph.addTransition(new Transition(splitState, emptyList(), "C", cState));
    internalGraph.addTransition(new Transition(cState, emptyList(), "End", endState));
    internalGraph.addTransition(new Transition(aState, emptyList(), mergingName, mergeState));
    internalGraph.addTransition(new Transition(bState, emptyList(), mergingName, mergeState));
    return internalGraph;
  }

  @Test
  void transform() {
    LTS externalLTS = new LTS();
    var preStates = List.of(new State(), new State(), new State());
    var postStates = List.of(new State(), new State(), new State());
    var gatewaySplitState = new State();
    var gatewayState = new State();
    var gatewayClosingState = new State();
    var splitName = "Gs";
    var mergingName = "Gm";
    for (int i = 0; i < preStates.size(); i++) {
      var pre = preStates.get(i);
      externalLTS.addTransition(new Transition(externalLTS.getStart(), emptyList(), "", pre));
      externalLTS.addTransition(new Transition(pre, emptyList(), "@Pre" + i, gatewaySplitState));
    }
    externalLTS.addTransition(
        new Transition(gatewaySplitState, emptyList(), splitName, gatewayState));
    externalLTS.addTransition(
        new Transition(gatewayState, emptyList(), mergingName, gatewayClosingState));
    for (int i = 0; i < postStates.size(); i++) {
      externalLTS.addTransition(
          new Transition(gatewayClosingState, emptyList(), "@Next" + i, postStates.get(i)));
    }
    var namingStrategy = new DefaultNamingStrategy();
    LTS internalLTS = internalGraph(splitName, mergingName);
    Graph2LTSTransformer graphTransformer = graph -> internalLTS;

    var doNothingInterleaving = new DoNothingInterleaving();

    Stream<State> internalEndEventTerminals =
        internalLTS.getTerminalStates().stream()
            .filter(
                terminal ->
                    internalLTS.getIncoming(terminal).stream()
                        .allMatch(incoming -> incoming.getLabel().equals("End")));
    List<State> expectedEndStates =
        Stream.concat(internalEndEventTerminals, externalLTS.getTerminalStates().stream())
            .collect(Collectors.toList());
    List<String> expectedExternalNames =
        Stream.concat(externalLTS.allUsedLabels().stream(), internalLTS.allUsedLabels().stream())
            .filter(label -> !label.equals(splitName) && !label.equals(mergingName))
            .collect(Collectors.toList());

    new DefaultGatewayTransformer(doNothingInterleaving)
        .transform(
            buildGatewayScope(splitName, mergingName),
            externalLTS,
            namingStrategy,
            graphTransformer);

    Assertions.assertFalse(externalLTS.isLabelPresent(splitName));
    Assertions.assertFalse(externalLTS.isLabelPresent(mergingName));
    List<String> actualUsedLabels = externalLTS.allUsedLabels();
    Assertions.assertTrue(equalIgnoreOrder(expectedExternalNames, actualUsedLabels));

    Assertions.assertTrue(equalIgnoreOrder(expectedEndStates, externalLTS.getTerminalStates()));
  }
}
