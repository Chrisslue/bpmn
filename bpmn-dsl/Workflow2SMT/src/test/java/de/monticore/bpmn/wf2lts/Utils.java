package de.monticore.bpmn.wf2lts;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;

public class Utils {

  /**
   * Compare equality of two lists but ignore the order of elements. Every element has to occur
   * equally often in both lists.
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
   * Assert that two lists contain the same elements with the same frequencies. Be aware that this
   * method is quite inefficient.
   */
  public static <T> void assertEqualIgnoreOrder(List<T> list1, List<T> list2) {
    assertContainedAndEqualCount(list1, list2);
    assertContainedAndEqualCount(list2, list1);
  }
}
