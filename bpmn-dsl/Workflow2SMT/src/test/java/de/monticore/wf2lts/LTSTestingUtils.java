package de.monticore.wf2ltl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.datastructure.LTS.State;
import de.monticore.wf2ltl.datastructure.LTS.Transition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

public class LTSTestingUtils {


  /**
   * Traverse a lst given a list of transition label. NOTE Uses the first transition it finds, not suited for
   * non-deterministic lts.
   *
   * @param lts    The lts to be traversed.
   * @param labels List of transition label.
   * @return The visited transitions if a path could be found else empty.
   */
  public static Optional<List<Transition>> pathOfLabel(LTS lts, List<String> labels) {
    var transitions = new ArrayList<Transition>();

    for (int idx = 0; idx < labels.size(); idx++) {
      var lastState = idx == 0 ? lts.getStart() : transitions.get(idx - 1).getTarget();
      int indexCopy = idx;
      var nextTransition = lts.getOutgoings(lastState)
          .stream()
          .filter(transition -> transition.getLabel().equals(labels.get(indexCopy)))
          .findFirst();
      if (nextTransition.isEmpty()) {
        return Optional.empty();
      }
      transitions.add(nextTransition.get());
    }
    return Optional.of(transitions);
  }

  public static void assertPathExists(LTS lts, List<String> labels) {
    assertTrue(pathOfLabel(lts, labels).isPresent(), "Path of labels doesnt exist" + labels);
  }

  public static void assertPathsExists(LTS lts, List<List<String>> paths) {
    for (var path : paths) {
      assertPathExists(lts, path);
    }
  }

  public static <T> List<List<T>> generatePermutations(List<T> originalList) {
    List<List<T>> permutations = new ArrayList<>();

    // Base case: If the original list is empty, return an empty permutation
    if (originalList.isEmpty()) {
      permutations.add(new ArrayList<>());
      return permutations;
    }

    T firstElement = originalList.get(0);
    List<T> remainingList = originalList.subList(1, originalList.size());
    List<List<T>> subPermutations = generatePermutations(remainingList);

    // For each sub-permutation, insert the first element at every possible position
    for (List<T> subPermutation : subPermutations) {
      for (int i = 0; i <= subPermutation.size(); i++) {
        List<T> newPermutation = new ArrayList<>(subPermutation);
        newPermutation.add(i, firstElement);
        permutations.add(newPermutation);
      }
    }

    return permutations;
  }

  public static boolean xComesBeforeY(List<String> list, String x, String y) {
    var xIdx = list.indexOf(x);
    var yIdx = list.indexOf(y);
    return (xIdx < 0 || yIdx < 0) || xIdx < yIdx;
  }

  public static List<List<String>> filterCorrectComesBefore(
      List<List<String>> paths,
      List<Entry<String, String>> beforeRelation
  ) {
    return paths
        .stream()
        .filter(path ->
            beforeRelation
                .stream()
                .allMatch(entry -> xComesBeforeY(path, entry.getKey(), entry.getValue()))
        ).collect(Collectors.toList());
  }

  public static void assertSameStartOutgoingLabel(LTS lts1, LTS lts2) {
    assertSameOutgoingLabel(lts1, lts1.getStart(), lts2, lts2.getStart());
  }

  public static void assertSameOutgoingLabel(LTS lts1, State state1, LTS lts2, State state2) {
    var outgoingLabel1 = lts1.getOutgoings(state1)
        .stream().map(Transition::getLabel).collect(Collectors.toList());
    var outgoingLabel2 = lts2.getOutgoings(state2)
        .stream().map(Transition::getLabel).collect(Collectors.toList());
    Utils.assertEqualIgnoreOrder(outgoingLabel1, outgoingLabel2);
  }

  public static void addTransition(State source, String label, Map<String, State> targets, LTS lts) {
    lts.addTransition(new Transition(source, Collections.emptyList(), label, targets.get(label)));
  }
}
