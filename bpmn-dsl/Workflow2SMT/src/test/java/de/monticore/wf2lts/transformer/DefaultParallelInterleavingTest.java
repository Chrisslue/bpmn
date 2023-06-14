package de.monticore.wf2ltl.transformer;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2ltl.LTSTestingUtils;
import de.monticore.wf2ltl.Utils;
import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.datastructure.LTS.State;
import de.monticore.wf2ltl.datastructure.LTS.Transition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultParallelInterleavingTest {


  private final List<String> elements = List.of("A", "B", "C", "D", "E", "F", "G");


  private void addTransition(LTS.State source, String label, Map<String, State> targets, LTS lts) {
    lts.addTransition(new Transition(source, Collections.emptyList(), label, targets.get(label)));
  }

  private LTS buildBranchingLTS() {
    // Use lts.toMermaid().build() to visualize the lts
    var lts = new LTS();
    Map<String, State> targets = elements.stream()
        .collect(Collectors.toMap(Function.identity(), (x) -> new LTS.State()));
    targets.values().forEach(lts::addState);
    addTransition(lts.getStart(), "A", targets, lts);
    addTransition(targets.get("A"), "B", targets, lts);
    addTransition(targets.get("B"), "C", targets, lts);
    // addTransition(targets.get("C"), "B", targets, lts); TODO test backlinks if implemented
    addTransition(targets.get("C"), "G", targets, lts);
    addTransition(targets.get("B"), "D", targets, lts);
    addTransition(targets.get("D"), "G", targets, lts);
    addTransition(targets.get("E"), "F", targets, lts);
    addTransition(lts.getStart(), "E", targets, lts);
    return lts;
  }

  private LTS buildThreeOptionsLTS() {
    var lts = new LTS();
    Map<String, State> targets = elements.stream()
        .collect(Collectors.toMap(Function.identity(), (x) -> new LTS.State()));
    targets.values().forEach(lts::addState);
    addTransition(lts.getStart(), "A", targets, lts);
    addTransition(targets.get("A"), "B", targets, lts);
    addTransition(targets.get("B"), "C", targets, lts);
    addTransition(lts.getStart(), "D", targets, lts);
    addTransition(targets.get("D"), "E", targets, lts);
    addTransition(lts.getStart(), "F", targets, lts);
    addTransition(targets.get("F"), "G", targets, lts);
    return lts;
  }

  private List<List<String>> allValidPathsForBranchingLTS() {
    var pathsWithC = new ArrayList<>(elements);
    pathsWithC.remove("D");
    var pathsWithD = new ArrayList<>(elements);
    pathsWithD.remove("C");
    var allPaths = LTSTestingUtils.generatePermutations(pathsWithC);
    allPaths.addAll(LTSTestingUtils.generatePermutations(pathsWithD));
    var beforeRelation = List.of(
        entry("A", "B"),
        entry("B", "C"),
        entry("B", "D"),
        entry("C", "G"),
        entry("D", "G"),
        entry("E", "F"));
    return LTSTestingUtils.filterCorrectComesBefore(allPaths, beforeRelation);
  }

  private List<List<String>> allValidPathForThreeOptionsLTS() {
    var beforeRelation = List.of(
        entry("A", "B"),
        entry("B", "C"),
        entry("D", "E"),
        entry("F", "G")
    );
    return LTSTestingUtils.filterCorrectComesBefore(LTSTestingUtils.generatePermutations(elements), beforeRelation);
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

    allValidPathsForBranchingLTS().forEach(path -> LTSTestingUtils.assertPathExists(interleavedLTS, path));

    assertTrue(LTSTestingUtils.pathOfLabel(interleavedLTS, elements).isEmpty(),
        "C and D cant be in the same path.");

    assertUsingAllElementsAsLabel(interleavedLTS);

    // Assert that all terminal states have only G or F incoming transitions.
    Assertions.assertTrue(
        interleavedLTS.getTerminalStates().stream()
            .allMatch(terminal ->
                interleavedLTS.getIncoming(terminal)
                    .stream()
                    .map(Transition::getLabel)
                    .allMatch(label -> label.equals("G") || label.equals("F"))
            ));
  }

  @Test
  void threeParallelPaths() {
    var originalLTS = buildThreeOptionsLTS();
    var interleaved = DefaultParallelInterleaving.interleave(originalLTS);

    LTSTestingUtils.assertSameStartOutgoingLabel(originalLTS, interleaved);

    allValidPathForThreeOptionsLTS().forEach(
        validPath -> LTSTestingUtils.assertPathExists(interleaved, validPath));
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
    originalLTS.addTransition(new Transition(originalLTS.getStart(), Collections.emptyList(), "A", aTarget));
    originalLTS.addTransition(new Transition(aTarget, Collections.emptyList(), "B", new State()));

    var interleaved = DefaultParallelInterleaving.interleave(originalLTS);
    assertEquals(1, interleaved.getOutgoings(interleaved.getStart()).size());
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B"));
    assertEquals(1, interleaved.getTerminalStates().size());
  }
}

