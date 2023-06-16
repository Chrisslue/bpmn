package de.monticore.wf2lts.transformer;

import de.monticore.wf2lts.LTSTestingUtils;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultSequentialInterleavingTest {


  private static LTS buildSimpleLTS() {
    var labels = List.of("A", "B", "C", "D");
    var lts = new LTS();
    Map<String, State> targets = labels.stream()
        .collect(Collectors.toMap(Function.identity(), (x) -> new LTS.State()));
    LTSTestingUtils.addTransition(lts.getStart(), "A", targets, lts);
    LTSTestingUtils.addTransition(lts.getStart(), "C", targets, lts);
    LTSTestingUtils.addTransition(targets.get("A"), "B", targets, lts);
    LTSTestingUtils.addTransition(targets.get("C"), "D", targets, lts);
    return lts;
  }

  private static LTS buildThreePaths() {
    var lts = buildSimpleLTS();
    var eTarget = new State();
    lts.addTransition(new Transition(lts.getStart(), Collections.emptyList(), "E", eTarget));
    lts.addTransition(new Transition(eTarget, Collections.emptyList(), "F", new State()));
    return lts;
  }

  @Test
  void interleave() {
    var lts = buildSimpleLTS();

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertPathExists(interleaved,
        List.of("A", "B", "C", "D")
    );
    LTSTestingUtils.assertPathExists(interleaved,
        List.of("C", "D", "A", "B")
    );
    Assertions.assertFalse(LTSTestingUtils.pathOfLabel(interleaved, List.of("A", "C")).isPresent());
    Assertions.assertFalse(LTSTestingUtils.pathOfLabel(interleaved, List.of("C", "A")).isPresent());

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);
  }

  @Test
  void testOnlyOnePath() {
    var lts = new LTS();
    var aTarget = new State();
    lts.addTransition(new Transition(lts.getStart(), Collections.emptyList(), "A", aTarget));
    lts.addTransition(new Transition(aTarget, Collections.emptyList(), "B", new State()));
    lts.addTransition(new Transition(aTarget, Collections.emptyList(), "C", new State()));

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    Assertions.assertEquals(1, interleaved.getOutgoings(interleaved.getStart()).size());
    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "B"));
    LTSTestingUtils.assertPathExists(interleaved, List.of("A", "C"));
    Assertions.assertEquals(2, interleaved.getTerminalStates().size());
  }

  @Test
  void testThreePaths() {
    var lts = buildThreePaths();

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    // Generate all combinations of the three paths.
    List<List<String>> threePathsLTSValidPaths =
        LTSTestingUtils
            .generatePermutations(List.of(List.of("A", "B"), List.of("C", "D"), List.of("E", "F")))
            .stream()
            .map(x -> x.stream().flatMap(List::stream).collect(Collectors.toList()))
            .collect(Collectors.toList());

    LTSTestingUtils.assertPathsExists(interleaved, threePathsLTSValidPaths);
  }

  @Test
  void testBackLinks() {
    var lts = buildSimpleLTS();
    var bTransition = lts.getTransitionsForLabel("B").get(0);
    lts.addTransition(
        new Transition(
            bTransition.getTarget(),
            Collections.emptyList(),
            "Z",
            bTransition.getSource()
        )
    );
    lts.addTransition(
        new Transition(
            bTransition.getTarget(),
            Collections.emptyList(),
            "BB",
            new State()
        )
    );

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    LTSTestingUtils.assertPathExists(interleaved,
        List.of("A", "B", "BB", "C", "D")
    );
    LTSTestingUtils.assertPathExists(interleaved,
        List.of("C", "D", "A", "B", "BB")
    );
    // Test for recursive paths
    LTSTestingUtils.assertPathExists(interleaved,
        List.of("A", "B", "Z", "B", "BB", "C", "D")
    );
    LTSTestingUtils.assertPathExists(interleaved,
        List.of("A", "B", "Z", "B", "Z", "B", "BB", "C", "D")
    );
    LTSTestingUtils.assertPathExists(interleaved,
        List.of("C", "D", "A", "B", "Z", "B", "BB")
    );

    // Assert backlink is indeed pointing back.
    Assertions.assertEquals(2, interleaved.getTransitionsForLabel("Z").size());
    var pathToZ = LTSTestingUtils.pathOfLabel(interleaved, List.of("A", "B", "Z")).orElseThrow();
    // The target of the Z transition is the source of the B transition
    Assertions.assertEquals(pathToZ.get(1).getSource(), pathToZ.get(2).getTarget());
  }

  @Test
  void testComplexLTS() {
    var lts = buildThreePaths();
    // Add second successor to A transition
    var aTarget = lts.getTransitionsForLabel("A").get(0).getTarget();
    lts.addTransition(
        new Transition(
            aTarget,
            Collections.emptyList(),
            "BB",
            new State()
        )
    );
    // Create cycle D,Z
    var bTransition = lts.getTransitionsForLabel("D").get(0);
    lts.addTransition(
        new Transition(
            bTransition.getTarget(),
            Collections.emptyList(),
            "Z",
            bTransition.getSource()
        )
    );
    // Add new successor to D s.t. the C,D path ends in a terminal state
    lts.addTransition(
        new Transition(
            bTransition.getTarget(),
            Collections.emptyList(),
            "DD",
            new State()
        )
    );

    var interleaved = DefaultSequentialInterleaving.interleave(lts);

    LTSTestingUtils.assertSameStartOutgoingLabel(lts, interleaved);

    List<List<String>> threePathsLTSValidPaths =
        LTSTestingUtils
            .generatePermutations(List.of(List.of("A", "B"), List.of("C", "D", "DD"), List.of("E", "F")))
            .stream()
            .map(x -> x.stream().flatMap(List::stream).collect(Collectors.toList()))
            .collect(Collectors.toList());

    for (var validPath : threePathsLTSValidPaths) {
      LTSTestingUtils.assertPathExists(interleaved, validPath);
    }

    var backLinkPaths = List.of(
        List.of("A", "B", "C", "D", "Z", "D", "DD", "E", "F"),
        List.of("C", "D", "Z", "D", "DD", "E", "F", "A", "B"),
        List.of("C", "D", "DD", "E", "F", "A", "B")
    );
    LTSTestingUtils.assertPathsExists(interleaved, backLinkPaths);

    var branchingAPaths = List.of(
        List.of("A", "B", "C", "D"),
        List.of("A", "BB", "C"),
        List.of("E", "F", "A", "BB", "C"),
        List.of("E", "F", "A", "B", "C")
    );
    LTSTestingUtils.assertPathsExists(interleaved, branchingAPaths);
  }
}