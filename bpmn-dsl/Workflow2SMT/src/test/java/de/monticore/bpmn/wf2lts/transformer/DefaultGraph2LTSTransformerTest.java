package de.monticore.bpmn.wf2lts.transformer;

import static java.util.Map.entry;

import de.monticore.bpmn.wf2lts.DefaultNamingStrategy;
import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.EdgeTo;
import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.wf2lts.scopes.SubProcessScope;
import de.monticore.bpmn.workflow._ast.ASTEventType;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTIOSpecificationBuilder;
import de.monticore.bpmn.workflow._ast.ASTInlineEventBuilder;
import de.monticore.bpmn.workflow._ast.ASTNamedEvent;
import de.monticore.bpmn.workflow._ast.ASTNamedEventBuilder;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._ast.ASTSubProcessBuilder;
import de.monticore.bpmn.workflow._ast.ASTSubProcessType;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.ASTTaskBuilder;
import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.bpmn.workflow._ast.SequenceFlowBuilder;
import de.monticore.lts.LTS2Mermaid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultGraph2LTSTransformerTest {

  private ASTSubProcess buildSubprocess(String subProcessName, String gTaskName) {
    var start = new ASTInlineEventBuilder().setType(ASTEventType.START).build();
    var end = new ASTInlineEventBuilder().setType(ASTEventType.END).build();
    var taskG = new ASTTaskBuilder().setName(gTaskName).build();

    var start2G = new SequenceFlowBuilder().setSource(start).setTarget(taskG).build();
    var g2End = new SequenceFlowBuilder().setSource(taskG).setTarget(end).build();
    start.getOutgoingsList().add(start2G);
    taskG.getOutgoingsList().add(g2End);

    return new ASTSubProcessBuilder()
        .setName(subProcessName)
        .setType(ASTSubProcessType.SUBPROCESS)
        .setIOSpecification(new ASTIOSpecificationBuilder().build())
        .setFlowElementsList(List.of(start, end, taskG))
        .build();
  }

  private GatewayTransformer doNothingGatewayTransformer() {
    return new GatewayTransformer() {

      @Override
      public GatewayInterleavingStrategy getGatewayInterleavingStrategy() {
        return null;
      }

      @Override
      public LTS transform(
          GatewayScope gatewayScope,
          LTS externalLTS,
          Graph2LTSTransformer graph2LTSTransformer
      ) {
        return externalLTS;
      }
    };
  }

  private SubprocessTransformer doNothingSubprocessTransformer() {
    return new SubprocessTransformer() {
      @Override
      public void transform(
          SubProcessScope subProcessScope,
          LTS externalGraph,
          NamingStrategy<IFlowNode> namingStrategy,
          Graph2LTSTransformer graphTransformer) {}
    };
  }

  private IntermediateGraphWithScopes buildTestGraph(
      String aTaskName,
      String bTaskName,
      String cTaskName,
      String startEventName,
      String endEventName) {
    ASTTask aTask = new ASTTaskBuilder().setName(aTaskName).build();
    ASTTask bTask = new ASTTaskBuilder().setName(bTaskName).build();
    ASTTask cTask = new ASTTaskBuilder().setName(cTaskName).build();
    ASTNamedEvent start =
        new ASTNamedEventBuilder().setType(ASTEventType.START).setName(startEventName).build();
    ASTNamedEvent end =
        new ASTNamedEventBuilder().setType(ASTEventType.END).setName(endEventName).build();

    Map<ASTFlowNode, List<EdgeTo<ASTFlowNode>>> edges =
        Map.ofEntries(
            entry(start, List.of(new EdgeTo<>(Collections.emptyList(), aTask))),
            entry(
                aTask,
                List.of(
                    new EdgeTo<>(Collections.emptyList(), bTask),
                    new EdgeTo<>(Collections.emptyList(), cTask))),
            entry(bTask, List.of(new EdgeTo<>(Collections.emptyList(), end))),
            entry(
                cTask,
                List.of(
                    new EdgeTo<>(Collections.emptyList(), aTask),
                    new EdgeTo<>(Collections.emptyList(), end))));
    return new IntermediateGraphWithScopes(start, edges);
  }

  @Test
  void transform() {

    String aTaskName = "A";
    String bTaskName = "B";
    String cTaskName = "C";
    String startEventName = "Start";
    String endEventName = "End";

    var namingStrategy = new DefaultNamingStrategy();

    var graphTransformer =
        new DefaultGraph2LTSTransformer(
            namingStrategy, doNothingGatewayTransformer(), doNothingSubprocessTransformer());

    var graph = buildTestGraph(aTaskName, bTaskName, cTaskName, startEventName, endEventName);
    LTS lts = graphTransformer.transform(graph);

    // Only end-event ends in terminal state.
    Assertions.assertEquals(1, lts.getTerminalStates().size());
    Assertions.assertTrue(
        lts.getTerminalStates().stream()
            .map(lts::getIncoming)
            .flatMap(List::stream)
            .allMatch(transition -> transition.getLabel().equals(endEventName)));
    for (var existingLabel :
        List.of(aTaskName, bTaskName, cTaskName, startEventName, endEventName)) {
      Assertions.assertTrue(lts.isLabelPresent(existingLabel), existingLabel + " not present.");
    }
    var asMermaid = lts.toModel(new LTS2Mermaid()).build(); // optionally look at result in https://mermaid.live/
  }
}
