package de.monticore.wf2lts.transformer;


import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2lts.LTSTestingUtils;
import de.monticore.wf2lts.Utils;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import de.monticore.wf2lts.datastructure.LTSTraverser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultParallelInterleavingTest {

  private final List<String> elements = List.of("A", "B", "C", "D", "E", "F", "G");

  private LTS buildBranchingLTS() {
    // Use lts.toMermaid().build() to visualize the lts
    var lts = new LTS();
    var abTransitions = LTSTestingUtils.toTransitions(lts.getStart(), List.of("A", "B"));
    var bTarget = abTransitions.get(abTransitions.size() - 1).getTarget();
    abTransitions.forEach(lts::addTransition);
    LTSTestingUtils.toTransitions(bTarget, List.of("C", "G")).forEach(lts::addTransition);
    LTSTestingUtils.toTransitions(bTarget, List.of("D", "G")).forEach(lts::addTransition);
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("E", "F"));
    return lts;
  }

  private LTS buildThreeOptionsLTS() {
    var lts = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("D", "E"));
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("F", "G"));
    return lts;
  }

  private List<List<String>> allValidPathsForBranchingLTS() {
    var pathsWithC = new ArrayList<>(elements);
    pathsWithC.remove("D");
    var pathsWithD = new ArrayList<>(elements);
    pathsWithD.remove("C");
    var allPaths = LTSTestingUtils.generatePermutations(pathsWithC);
    allPaths.addAll(LTSTestingUtils.generatePermutations(pathsWithD));
    var beforeRelation =
        List.of(
            entry("A", "B"),
            entry("B", "C"),
            entry("B", "D"),
            entry("C", "G"),
            entry("D", "G"),
            entry("E", "F"));
    return LTSTestingUtils.filterCorrectComesBefore(allPaths, beforeRelation);
  }

  private List<List<String>> allValidPathForThreeOptionsLTS() {
    var beforeRelation =
        List.of(entry("A", "B"), entry("B", "C"), entry("D", "E"), entry("F", "G"));
    return LTSTestingUtils.filterCorrectComesBefore(
        LTSTestingUtils.generatePermutations(elements), beforeRelation);
  }

  private void assertUsingAllElementsAsLabel(LTS lts) {
    Utils.assertEqualIgnoreOrder(elements, lts.allUsedLabels());
  }

  @Test
  void interleaveTest() {
    var originalLTS = buildBranchingLTS();
    var interleavedLTS = DefaultParallelInterleaving.interleave(originalLTS);
    // Assert A, E are the only outgoing transition labels of start:
    LTSTestingUtils.assertSameStartOutgoingLabel(originalLTS, interleavedLTS);

    allValidPathsForBranchingLTS()
        .forEach(path -> LTSTestingUtils.assertPathExists(interleavedLTS, path));

    assertTrue(
        LTSTraverser.pathOfLabel(interleavedLTS, elements).isEmpty(),
        "C and D cant be in the same path.");

    assertUsingAllElementsAsLabel(interleavedLTS);

    // Assert that all terminal states have only G or F incoming transitions.
    Assertions.assertTrue(
        interleavedLTS.getTerminalStates().stream()
            .allMatch(
                terminal ->
                    interleavedLTS.getIncoming(terminal).stream()
                        .map(Transition::getLabel)
                        .allMatch(label -> label.equals("G") || label.equals("F"))));
  }

  @Test
  void threeParallelPaths() {
    var originalLTS = buildThreeOptionsLTS();
    var interleaved = DefaultParallelInterleaving.interleave(originalLTS);

    LTSTestingUtils.assertSameStartOutgoingLabel(originalLTS, interleaved);

    LTSTestingUtils.assertPathsExists(interleaved, allValidPathForThreeOptionsLTS());
  }

  @Test
  void testEmptyLTS() {
    var originalLTS = new LTS();
    var interleaved = DefaultParallelInterleaving.interleave(originalLTS);
    assertTrue(interleaved.getOutgoings(interleaved.getStart()).isEmpty());
    assertTrue(interleaved.allUsedLabels().isEmpty());
  }

  @Test
  void testNoParallelPaths() {
    var originalLTS = new LTS();
    var aTarget = new State();
    originalLTS.addTransition(
        new Transition(originalLTS.getStart(), Collections.emptyList(), "A", aTarget));
    originalLTS.addTransition(new Transition(aTarget, Collections.emptyList(), "B", new State()));

    var interleaved = DefaultParallelInterleaving.interleave(originalLTS);
    assertEquals(1, interleaved.getOutgoings(interleaved.getStart()).size());
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B"));
    assertEquals(1, interleaved.getTerminalStates().size());
  }

  @Test
  void testCycle() {
    var lts = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("A", "B", "C", "D"));
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("E", "F"));
    var cTransition = lts.getTransitionsForLabel("C").get(0);
    lts.addTransition(new Transition(cTransition.getTarget(), Collections.emptyList(), "B", cTransition.getSource()));

    var interleaved = DefaultParallelInterleaving.interleave(lts);
    var interleavedTraverser = new LTSTraverser(interleaved);
    LTSTestingUtils.assertSameOutgoingLabel(lts, lts.getStart(), interleaved, interleaved.getStart());

    // Assert that "B" after "C" transition is a simple backlink
    var simpleCycle = LTSTraverser.pathOfLabel(interleaved, List.of("A", "B", "C")).orElseThrow();
    var interleavedCTransition = simpleCycle.getTransitions().get(simpleCycle.getTransitions().size() - 1);
    var optBacklink = simpleCycle.outgoingsWith("B");
    Assertions.assertFalse(optBacklink.size() != 1,
        "Expected outgoing transition with B but found: " + simpleCycle.outgoingsWith("B"));
    var backLink = optBacklink.get(0);
    Assertions.assertEquals(interleavedCTransition.getSource(), backLink.getTarget());

    // Assert that no second "E" can be reached after one "E"
    Consumer<State> assertNoOutgoingWithE = (s) -> Assertions.assertFalse(
        interleaved.getOutgoings(s).stream().anyMatch(t -> t.getLabel().equals("E")));
    var eTransitions = interleaved.getTransitionsForLabel("E");
    eTransitions.forEach(
        eTransition -> interleavedTraverser.depthFirstSearchLTS(
            eTransition.getTarget(),
            assertNoOutgoingWithE
        )
    );

    // Assert after every "E", for which a "C" but no "D" occurred previously, "B" is an outgoing transition..
    var traverser = new LTSTraverser(interleaved);
    var badWitness = interleaved.getTransitionsForLabel("E").stream()
        .filter(eTransition -> traverser // "C" but not "D" occurred on the path from start to "E"
            .pathsBetween(interleaved.getStart(), eTransition.getSource())
            .stream()
            .anyMatch(pathToE -> pathToE.labelOccurred("C") && !pathToE.labelOccurred("D"))
        )
        .filter(eTransition -> interleaved
            .getOutgoings(eTransition.getTarget())
            .stream()
            .noneMatch(successor -> successor.getLabel().equals("B"))
        ).findFirst();
    Assertions.assertTrue(badWitness.isEmpty());

    // Assert that no "E" for which a "D" occurred before has an outgoing with "B"
    var bOutgoingAfterEWithPreviousD = interleaved.getTransitionsForLabel("E").stream()
        .filter(eTransition -> traverser //"D" occurred on the path from start to "E"
            .pathsBetween(interleaved.getStart(), eTransition.getSource())
            .stream()
            .anyMatch(pathToE -> pathToE.labelOccurred("D"))
        )
        .filter(eTransition -> interleaved
            .getOutgoings(eTransition.getTarget())
            .stream()
            .anyMatch(successor -> successor.getLabel().equals("B"))
        ).findFirst();
    Assertions.assertTrue(bOutgoingAfterEWithPreviousD.isEmpty());
  }
}
