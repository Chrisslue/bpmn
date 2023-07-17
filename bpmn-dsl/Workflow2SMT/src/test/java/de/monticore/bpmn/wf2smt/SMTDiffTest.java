package de.monticore.bpmn.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class SMTDiffTest {

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
    var differ = WF2SMTDiffGenerator.generateDiffer(first, second, List.of("End"));
    var witnessForFirst = differ.firstSubsetOfSecond(4);
    assertTrue(witnessForFirst.isEmpty());
    var witnessForSecond = differ.secondSubsetOfFirst(4);
    assertTrue(witnessForSecond.isPresent());
    var labelList = witnessForSecond.get();
    var witnessPath = new LTSTraverser(second).pathOfLabel(labelList).orElseThrow();
    assertEquals(2, labelList.size());

    assertEquals(second.getTransitionsForLabel("B").get(0), witnessPath.getTransitions().get(0));
    assertEquals("End", witnessPath.getTransitions().get(1).getLabel());
    assertTrue(witnessPath.endsInTerminal());
  }

  @Test
  void findWitnessReflexive() {
    var lts = getComplexLTS();

    var differ = WF2SMTDiffGenerator.generateDiffer(lts, lts, List.of("End"));
    assertTrue(differ.firstSubsetOfSecond(3).isEmpty());
    assertTrue(differ.firstSubsetOfSecond(3).isEmpty());
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
    var differ = WF2SMTDiffGenerator.generateDiffer(first, second, List.of("End"));
    var witnessForFirst = differ.firstSubsetOfSecond(10);
    assertTrue(witnessForFirst.isEmpty());
    var optWitnessForSecond = differ.secondSubsetOfFirst(10);
    assertTrue(optWitnessForSecond.isPresent());
    var witnessForSecond = new LTSTraverser(second).pathOfLabel(optWitnessForSecond.get()).orElseThrow();

    assertTrue(witnessForSecond.getTransitions().size() >= 7);
    assertTrue(witnessForSecond.endsInTerminal());

    var shortWitness = differ.secondSubsetOfFirst(5);
    assertTrue(shortWitness.isEmpty());

  }
}