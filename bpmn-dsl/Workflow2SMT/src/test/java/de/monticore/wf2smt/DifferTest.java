package de.monticore.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2lts.LTSTestingUtils;
import de.monticore.wf2lts.datastructure.LTS;
import java.util.List;
import org.junit.jupiter.api.Test;

class DifferTest {

  @Test
  void findWitnessSimple() {
    var first = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "End"));
    var second = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("A", "End"));
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("B", "End"));
    var differ = new Differ(first, second);
    var witnessForFirst = differ.findWitness(differ.getEncodedFirst(), differ.getEncodedSecond(), 4);
    assertTrue(witnessForFirst.isEmpty());
    var witnessForSecond = differ.findWitness(differ.getEncodedSecond(), differ.getEncodedFirst(), 4);
    assertTrue(witnessForSecond.isPresent());
    var transitions = witnessForSecond.get().getTransitions();
    assertEquals(1, transitions.size());
    assertEquals(second.getTransitionsForLabel("B").get(0), transitions.get(0));
  }
}