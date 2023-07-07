package de.monticore.wf2lts.transformer;

import static de.monticore.wf2lts.Utils.assertEqualIgnoreOrder;

import de.monticore.bpmn.workflow._ast.ASTEventTriggerTerminateBuilder;
import de.monticore.bpmn.workflow._ast.ASTEventType;
import de.monticore.bpmn.workflow._ast.ASTGatewayDirection;
import de.monticore.bpmn.workflow._ast.ASTGatewayTypeBuilder;
import de.monticore.bpmn.workflow._ast.ASTIOSpecificationBuilder;
import de.monticore.bpmn.workflow._ast.ASTNamedEventBuilder;
import de.monticore.bpmn.workflow._ast.ASTNamedGatewayBuilder;
import de.monticore.bpmn.workflow._ast.ASTSubProcessBuilder;
import de.monticore.bpmn.workflow._ast.ASTSubProcessType;
import de.monticore.bpmn.workflow._ast.ASTTaskBuilder;
import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.bpmn.workflow._ast.SequenceFlow;
import de.monticore.bpmn.workflow._ast.SequenceFlowBuilder;
import de.monticore.wf2lts.DefaultNamingStrategy;
import de.monticore.wf2lts.NamingStrategy;
import de.monticore.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import de.monticore.wf2lts.scopes.SubProcessScope;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSubprocessTransformerTest {

  @BeforeEach
  void setUp() {}

  private SubProcessScope buildSubProcessScope(
      String startEventName,
      String taskAName,
      String taskBName,
      String xorGatewayName,
      String endEventName,
      String termEventName) {
    var startEvent =
        new ASTNamedEventBuilder().setName(startEventName).setType(ASTEventType.START).build();
    var taskA = new ASTTaskBuilder().setName(taskAName).build();
    var taskB = new ASTTaskBuilder().setName(taskBName).build();
    var xorGateway =
        new ASTNamedGatewayBuilder()
            .setName(xorGatewayName)
            .setType(new ASTGatewayTypeBuilder().setExclusive(true).build())
            .setDirection(ASTGatewayDirection.SPLIT)
            .build();
    var termEvent =
        new ASTNamedEventBuilder()
            .setName(termEventName)
            .setTrigger(new ASTEventTriggerTerminateBuilder().build())
            .setType(ASTEventType.END)
            .build();
    var endEvent =
        new ASTNamedEventBuilder().setName(endEventName).setType(ASTEventType.END).build();

    var start2A = new SequenceFlowBuilder().setSource(startEvent).setTarget(taskA).build();
    var a2Xor = new SequenceFlowBuilder().setSource(taskA).setTarget(xorGateway).build();
    var xor2B = new SequenceFlowBuilder().setSource(xorGateway).setTarget(taskB).build();
    var xor2End = new SequenceFlowBuilder().setSource(xorGateway).setTarget(endEvent).build();
    SequenceFlow b2Term = new SequenceFlowBuilder().setSource(taskB).setTarget(termEvent).build();

    startEvent.getOutgoingsList().add(start2A);
    taskA.getIncomingsList().add(start2A);

    taskA.getOutgoingsList().add(a2Xor);
    xorGateway.getIncomingsList().add(a2Xor);

    xorGateway.getOutgoingsList().add(xor2B);
    taskB.getIncomingsList().add(xor2B);
    xorGateway.getOutgoingsList().add(xor2End);
    endEvent.getIncomingsList().add(xor2End);

    taskB.getOutgoingsList().add(b2Term);
    termEvent.getIncomingsList().add(b2Term);

    var subprocess =
        new ASTSubProcessBuilder()
            .setName("T")
            .setType(ASTSubProcessType.SUBPROCESS)
            .setIOSpecification(new ASTIOSpecificationBuilder().build())
            .setFlowElementsList(List.of(startEvent, taskA, taskB, xorGateway, endEvent, termEvent))
            .build();

    return new SubProcessScope(subprocess);
  }

  private LTS buildInternalLTS(
      String startEventName,
      String taskAName,
      String taskBName,
      String endEventName,
      String termEventName) {
    LTS internalLTS = new LTS();
    var startEventTarget = new State();
    var taskATarget = new State();
    var taskBTarget = new State();
    var endEventTarget = new State();
    var termEventTarget = new State();
    internalLTS.addTransition(
        new Transition(
            internalLTS.getStart(), Collections.emptyList(), startEventName, startEventTarget));
    internalLTS.addTransition(
        new Transition(startEventTarget, Collections.emptyList(), taskAName, taskATarget));
    internalLTS.addTransition(
        new Transition(taskATarget, Collections.emptyList(), taskBName, taskBTarget));
    internalLTS.addTransition(
        new Transition(taskATarget, Collections.emptyList(), endEventName, endEventTarget));
    internalLTS.addTransition(
        new Transition(taskBTarget, Collections.emptyList(), termEventName, termEventTarget));

    return internalLTS;
  }

  private LTS buildExternalLTS(String subprocessName) {
    var lts = new LTS();
    var subprocessSource = new State();
    var subprocessTarget = new State();
    var postStates = List.of(new State(), new State());
    // States are added implicitly by addTransition
    for (int i = 0; i < 2; i++) {
      lts.addTransition(
          new Transition(lts.getStart(), Collections.emptyList(), "@Pre" + i, subprocessSource));
    }
    lts.addTransition(
        new Transition(
            subprocessSource, Collections.emptyList(), subprocessName, subprocessTarget));
    for (int i = 0; i < postStates.size(); i++) {
      var post = postStates.get(i);
      lts.addTransition(
          new Transition(subprocessTarget, Collections.emptyList(), "@Post" + i, post));
    }
    return lts;
  }

  @Test
  void transform() {

    String startEventName = "startEvent";
    String taskAName = "A";
    String taskBName = "B";
    String xorGatewayName = "xorGateway";
    String endEventName = "endEvent";
    String termEventName = "termEvent";
    String subprocessName = "T";

    NamingStrategy<IFlowNode> namingStrategy = new DefaultNamingStrategy();

    var externalLTS = buildExternalLTS(subprocessName);
    var internalLTS =
        buildInternalLTS(startEventName, taskAName, taskBName, endEventName, termEventName);

    var graph2LTSTransformer =
        new Graph2LTSTransformer() {
          @Override
          public LTS transform(IntermediateGraphWithScopes graph) {
            return internalLTS;
          }
        };

    var subProcessScope =
        buildSubProcessScope(
            startEventName, taskAName, taskBName, xorGatewayName, endEventName, termEventName);

    var expectedTerminalStates = externalLTS.getTerminalStates();

    new DefaultSubprocessTransformer()
        .transform(subProcessScope, externalLTS, namingStrategy, graph2LTSTransformer);

    for (var absentName : List.of(subprocessName, startEventName, endEventName, xorGatewayName)) {
      Assertions.assertFalse(
          externalLTS.isLabelPresent(absentName),
          absentName + " should not be a label of a transition in the final LTS.");
    }

    for (var existingName : List.of(taskAName, taskBName)) {
      Assertions.assertTrue(
          externalLTS.isLabelPresent(existingName),
          "Expected " + existingName + " to be a label in lts.");
    }

    // We still expect two terminal states (where @Pre0 and @Pre1 point to)
    assertEqualIgnoreOrder(expectedTerminalStates, externalLTS.getTerminalStates());

    // As the bpmn has a terminating transition after B there should be no proper end of the
    // subprocess.
    // Therefore, we expect the external successor-transitions of the subprocess as outgoings.
    var bTransitions = externalLTS.getTransitionsForLabel(taskBName);
    Assertions.assertEquals(
        1, bTransitions.size(), "Only one transition should be labeled with " + taskBName + ".");
    var bOutgoings = externalLTS.getOutgoings(bTransitions.get(0).getTarget());
    Assertions.assertEquals(
        2, bOutgoings.size(), "Expected @Post0 and @Post1 as outgoing transitions.");
    var bSuccessors = bOutgoings.stream().map(Transition::getTarget).collect(Collectors.toList());
    assertEqualIgnoreOrder(expectedTerminalStates, bSuccessors);
  }
}
