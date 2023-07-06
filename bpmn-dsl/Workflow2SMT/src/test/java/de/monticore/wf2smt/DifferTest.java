package de.monticore.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.wf2lts.LTSTestingUtils;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class DifferTest {

  private LTS getComplexLTS() {
    var lts = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(lts, List.of("A", "B", "C", "D", "End"));
    var aTarget = lts.getTransitionsForLabel("A").get(0).getTarget();
    var bTarget = lts.getTransitionsForLabel("B").get(0).getTarget();
    var cTarget = lts.getTransitionsForLabel("C").get(0).getTarget();
    LTSTestingUtils.toTransitions(aTarget, List.of("E", "F", "End"));
    LTSTestingUtils.toTransitions(bTarget, List.of("X", "Y"));
    lts.addTransition(new Transition(cTarget, Collections.emptyList(), "Z", aTarget));
    return lts;
  }

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
    assertEquals(2, transitions.size());
    assertEquals(second.getTransitionsForLabel("B").get(0), transitions.get(0));
    assertEquals("End", transitions.get(1).getLabel());
    assertTrue(witnessForSecond.get().endsInTerminal());
  }

  @Test
  void findWitnessReflexive() {
    var lts = getComplexLTS();

    var differ = new Differ(lts, lts);
    assertTrue(differ.findWitness(differ.getEncodedFirst(), differ.getEncodedSecond(), 3).isEmpty());
    assertTrue(differ.findWitness(differ.getEncodedSecond(), differ.getEncodedFirst(), 3).isEmpty());
  }

  @Test
  void findWitnessCyclic() {
    var first = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "End"));
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "A", "A", "End"));
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "A", "A", "A", "A", "End"));
    var second = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("A", "End"));
    var aTarget = second.getTransitionsForLabel("A").get(0).getTarget();
    var cycle = second.getTransitionsForLabel("A").get(0)
        .changedSource(aTarget)
        .changedTarget(second.getStart());
    second.addTransition(cycle);
    var differ = new Differ(first, second);
    var witnessForFirst = differ.findWitness(differ.getEncodedFirst(), differ.getEncodedSecond(), 10);
    assertTrue(witnessForFirst.isEmpty());
    var witnessForSecond = differ.findWitness(differ.getEncodedSecond(), differ.getEncodedFirst(), 10);
    assertTrue(witnessForSecond.isPresent());
    assertTrue(witnessForSecond.get().getTransitions().size() >= 7);
    assertTrue(witnessForSecond.get().endsInTerminal());

    var shortWitness = differ.findWitness(differ.getEncodedSecond(), differ.getEncodedFirst(), 5);
    assertTrue(shortWitness.isEmpty());

  }
}