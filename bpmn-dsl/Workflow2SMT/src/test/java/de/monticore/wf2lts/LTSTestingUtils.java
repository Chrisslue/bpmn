package de.monticore.wf2lts;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import de.monticore.wf2lts.datastructure.LTSTraverser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class LTSTestingUtils {


  public static void assertPathExists(LTS lts, List<String> labels) {
    assertTrue(LTSTraverser.pathOfLabel(lts, labels).isPresent(), "Path of labels doesnt exist" + labels);
  }

  public static void assertTerminatingPathExists(LTS lts, List<String> labels) {
    var optPath = LTSTraverser.pathOfLabel(lts, labels);
    assertTrue(
        optPath.isPresent() && optPath.get().endsInTerminal()
        , "Path of labels either doesn't exist or does not end in terminal state" + optPath);
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
      List<List<String>> paths, List<Entry<String, String>> beforeRelation) {
    return paths.stream()
        .filter(
            path ->
                beforeRelation.stream()
                    .allMatch(entry -> xComesBeforeY(path, entry.getKey(), entry.getValue())))
        .collect(Collectors.toList());
  }

  public static void assertSameStartOutgoingLabel(LTS lts1, LTS lts2) {
    assertSameOutgoingLabel(lts1, lts1.getStart(), lts2, lts2.getStart());
  }

  public static void assertSameOutgoingLabel(LTS lts1, State state1, LTS lts2, State state2) {
    var outgoingLabel1 =
        lts1.getOutgoings(state1).stream().map(Transition::getLabel).collect(Collectors.toList());
    var outgoingLabel2 =
        lts2.getOutgoings(state2).stream().map(Transition::getLabel).collect(Collectors.toList());
    Utils.assertEqualIgnoreOrder(outgoingLabel1, outgoingLabel2);
  }

  public static void addPathOfLabelFromStart(LTS lts, List<String> labelList) {
    toTransitions(lts.getStart(), labelList).forEach(lts::addTransition);
  }

  public static List<Transition> toTransitions(State start, List<String> labelList) {
    var transitions = new ArrayList<Transition>();
    var lastState = start;
    for (String label : labelList) {
      var next = new State();
      transitions.add(new Transition(lastState, Collections.emptyList(), label, next));
      lastState = next;
    }
    return transitions;
  }
}
