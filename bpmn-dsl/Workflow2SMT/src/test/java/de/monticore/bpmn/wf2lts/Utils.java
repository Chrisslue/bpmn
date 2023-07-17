package de.monticore.bpmn.wf2lts;

import static java.util.Map.entry;

import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.workflow._ast.IFlowNode;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;

public class Utils {

  /**
   * Compare equality of two lists but ignore the order of elements. Every element has to occur equally often in both
   * lists.
   */
  public static <T> boolean equalIgnoreOrder(List<T> list1, List<T> list2) {
    return list1.size() == list2.size() && list1.containsAll(list2) && list2.containsAll(list1);
  }

  private static <T> void assertContainedAndEqualCount(List<T> list1, List<T> list2) {
    var witnessNotInL2 =
        list1.stream()
            .filter(x -> Collections.frequency(list1, x) != Collections.frequency(list2, x))
            .findFirst();
    Assertions.assertFalse(
        witnessNotInL2.isPresent(),
        () ->
            "Element "
                + witnessNotInL2.get()
                + " has different frequencies in "
                + list1
                + " and in "
                + list2);
  }

  /**
   * Assert that two lists contain the same elements with the same frequencies. Be aware that this method is quite
   * inefficient.
   */
  public static <T> void assertEqualIgnoreOrder(List<T> list1, List<T> list2) {
    assertContainedAndEqualCount(list1, list2);
    assertContainedAndEqualCount(list2, list1);
  }

  public static void assertSameEdges(IntermediateGraphWithScopes graph, List<Entry<String, String>> expectedEdges) {
    assertSameEdges(graph, new DefaultNamingStrategy(), expectedEdges);
  }

  public static void assertSameEdges(
      IntermediateGraphWithScopes graph,
      NamingStrategy<IFlowNode> naming,
      List<Entry<String, String>> expectedEdges
  ) {
    List<Entry<String, String>> allEdges = graph.getEdges()
        .entrySet()
        .stream()
        .flatMap(sourceTargetsEntry ->
            sourceTargetsEntry.getValue().stream()
                .map(edgeTo -> entry(sourceTargetsEntry.getKey(), edgeTo.getTarget())))
        .map(astEntry -> entry(naming.apply(astEntry.getKey()), naming.apply(astEntry.getValue())))
        .collect(Collectors.toList());
    Utils.assertEqualIgnoreOrder(expectedEdges, allEdges);
  }
}
