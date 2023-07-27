package de.monticore.bpmn.wf2lts.transformer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.Utils;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSequentialInterleavingTest {

  @BeforeEach
  void setUp() {
    Log.init();
  }

  private static LTSWithFinalStates buildSimpleLTS() {
    var lts = new LTSWithFinalStates();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("A", "B"));
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("C", "D"));
    lts.getTerminalStates().forEach(lts::addAsFinalState);
    return lts;
  }

  private static LTSWithFinalStates buildThreePaths() {
    var lts = buildSimpleLTS();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("E", "F"));
    lts.addAsFinalState(lts.getTransitionsForLabel("F").get(0).getTarget());
    return lts;
  }

  @Test
  void interleave() {
    var lts = buildSimpleLTS();

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B", "C", "D"));
    LTSTestingUtils.assertPathExists(interleaved, List.of("C", "D", "A", "B"));
    Assertions.assertFalse(LTSTraverser.pathOfLabel(interleaved, List.of("A", "C")).isPresent());
    Assertions.assertFalse(LTSTraverser.pathOfLabel(interleaved, List.of("C", "A")).isPresent());

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    var expectedFinalStates = Stream.concat(
        interleaved.getTransitionsForLabel("B").stream().map(Transition::getTarget),
        interleaved.getTransitionsForLabel("D").stream().map(Transition::getTarget)
    ).collect(Collectors.toSet());
    Assertions.assertEquals(expectedFinalStates, interleaved.getFinalStates());

  }

  @Test
  void testOnlyOnePath() {
    var lts = new LTSWithFinalStates();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("A", "B"));
    lts.addTransition(
        new Transition(lts.getTransitionsForLabel("A").get(0).getTarget(), Collections.emptyList(), "C", new State()));
    lts.getTerminalStates().forEach(lts::addAsFinalState);

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B"));
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "C"));
    Assertions.assertEquals(2, interleaved.getTerminalStates().size());
    Assertions.assertEquals(1, interleaved.getOutgoings(interleaved.getStart()).size());

    var expectedFinalStates = Stream.of("B", "C")
        .flatMap(label -> interleaved.getTransitionsForLabel(label).stream())
        .map(Transition::getTarget)
        .collect(Collectors.toSet());

    Assertions.assertEquals(expectedFinalStates, interleaved.getFinalStates());
    // Final states equal terminal states as there is only one path.
    Utils.assertEqualIgnoreOrder(interleaved.getTerminalStates(), new ArrayList<>(interleaved.getFinalStates()));

  }

  @Test
  void testThreePaths() {
    var lts = buildThreePaths();

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    // Generate all combinations of the three paths.
    List<List<String>> threePathsLTSValidPaths =
        LTSTestingUtils.generatePermutations(
                List.of(List.of("A", "B"), List.of("C", "D"), List.of("E", "F")))
            .stream()
            .map(x -> x.stream().flatMap(List::stream).collect(Collectors.toList()))
            .collect(Collectors.toList());

    LTSTestingUtils.assertPathsExist(interleaved, threePathsLTSValidPaths);
    var expectedFinalStates = Stream.of("B", "D", "F")
        .flatMap(label -> interleaved.getTransitionsForLabel(label).stream())
        .map(Transition::getTarget)
        .collect(Collectors.toSet());
    Assertions.assertEquals(expectedFinalStates, interleaved.getFinalStates());
  }

  @Test
  void testBackLinks() {
    var lts = buildSimpleLTS();
    var bTransition = lts.getTransitionsForLabel("B").get(0);
    lts.addTransition( // Create cycle by transition from target of B, labeled Z, to source of B.
        new Transition(
            bTransition.getTarget(), Collections.emptyList(), "Z", bTransition.getSource()));
    var bbTarget = new State();
    lts.addTransition(
        new Transition(bTransition.getTarget(), Collections.emptyList(), "BB", bbTarget));

    lts.unmarkAsFinal(bTransition.getTarget()); // Unmark as final state
    lts.addAsFinalState(bbTarget);

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B", "BB", "C", "D"));
    LTSTestingUtils.assertPathExists(interleaved, List.of("C", "D", "A", "B", "BB"));
    // Test for recursive paths
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B", "Z", "B", "BB", "C", "D"));
    LTSTestingUtils.assertPathExists(
        interleaved, List.of("A", "B", "Z", "B", "Z", "B", "BB", "C", "D"));
    LTSTestingUtils.assertPathExists(interleaved, List.of("C", "D", "A", "B", "Z", "B", "BB"));

    // Assert backlink is indeed pointing back.
    Assertions.assertEquals(2, interleaved.getTransitionsForLabel("Z").size());
    var pathToZ = LTSTraverser.pathOfLabel(interleaved, List.of("A", "B", "Z")).orElseThrow();
    // The target of the Z transition is the source of the B transition
    Assertions.assertEquals(pathToZ.getTransitions().get(1).getSource(), pathToZ.getTransitions().get(2).getTarget());

    var expectedFinalStates = Stream.of("D", "BB")
        .flatMap(label -> interleaved.getTransitionsForLabel(label).stream())
        .map(Transition::getTarget)
        .collect(Collectors.toSet());
    Assertions.assertEquals(expectedFinalStates, interleaved.getFinalStates());
  }

  @Test
  void testComplexLTS() {
    var lts = buildThreePaths();
    // Add second successor to A transition
    var aTarget = lts.getTransitionsForLabel("A").get(0).getTarget();
    lts.addTransition(new Transition(aTarget, Collections.emptyList(), "End", new State()));
    // Create cycle D,Z
    var bTransition = lts.getTransitionsForLabel("D").get(0);
    lts.addTransition(
        new Transition(
            bTransition.getTarget(), Collections.emptyList(), "Z", bTransition.getSource()));
    // The targets of "D", "B" and "F" are final states.
    // Target of "End" is not a final state (but a terminal state)!
    var interleaved = DefaultSequentialInterleaving.interleave(lts);
    var interleavedTraverser = new LTSTraverser(interleaved);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    List<List<String>> threePathsLTSValidPaths =
        LTSTestingUtils.generatePermutations(
                List.of(List.of("A", "B"), List.of("C", "D"), List.of("E", "F")))
            .stream()
            .map(x -> x.stream().flatMap(List::stream).collect(Collectors.toList()))
            .collect(Collectors.toList());

    for (var validPath : threePathsLTSValidPaths) {
      LTSTestingUtils.assertPathExists(interleaved, validPath);
    }

    // Assert "End" after every "A" transition possible
    var aWithoutEndOutgoing = interleaved.getTransitionsForLabel("A")
        .stream()
        .map(Transition::getTarget)
        .map(interleavedTraverser::pathFrom)
        .filter(path -> path.outgoingsWith("End").size() != 1)
        .findAny();
    assertTrue(aWithoutEndOutgoing.isEmpty());

    // Assert no transition after "End"
    var interleavedEndTargets = interleaved
        .getTransitionsForLabel("End")
        .stream()
        .map(Transition::getTarget).collect(
            Collectors.toList());
    assertTrue(interleaved.getTerminalStates().containsAll(interleavedEndTargets));
    assertTrue(interleavedEndTargets.stream().noneMatch(interleaved::isFinalState));

    // Assert after every "D" transition a "Z" is possible.
    var zOutgoingsFromB = interleaved.getTransitionsForLabel("D")
        .stream()
        .map(Transition::getTarget)
        .map(interleavedTraverser::pathFrom)
        .map(path -> path.outgoingsWith("Z"))
        .collect(Collectors.toList());
    assertTrue(zOutgoingsFromB.stream().allMatch(zPath -> zPath.size() == 1));

    // Assert that every you have "D" followed by "Z" after every target of a "Z" transition
    var noBZContinuation = zOutgoingsFromB
        .stream()
        .map(transitions -> transitions.get(0).getTarget())
        .filter(zTarget -> interleavedTraverser.pathOfLabel(zTarget, List.of("D", "Z")).isEmpty())
        .findAny();
    assertTrue(noBZContinuation.isEmpty());
  }
}
