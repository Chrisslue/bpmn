package de.monticore.bpmn.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.se_rwth.commons.logging.MCFatalError;
import java.util.List;
import org.junit.jupiter.api.Test;

class WF2SMTDiffGeneratorTest {

  @Test
  void testGenerateDifferModels() {
    var firstPath = getClass().getResource("Simple.wfm").getPath();
    var pathOfEquivalent = getClass().getResource("SimpleEquivalent.wfm").getPath();
    var differ = WF2SMTDiffGenerator.generateDiffer(firstPath, pathOfEquivalent);
    assertTrue(differ.firstSubsetOfSecond(5).isEmpty());
    assertTrue(differ.secondSubsetOfFirst(5).isEmpty());
    var pathOfUnequal = getClass().getResource("SimpleNotEquivalent.wfm").getPath();
    differ = WF2SMTDiffGenerator.generateDiffer(firstPath, pathOfUnequal);
    assertTrue(differ.firstSubsetOfSecond(5).isEmpty());
    var optWitness = differ.secondSubsetOfFirst(5);
    assertTrue(optWitness.isPresent());
    assertEquals(List.of("Start", "End"), optWitness.get());

    // Test invalid model
    assertThrows(MCFatalError.class, () -> WF2SMTDiffGenerator.generateDiffer(firstPath, "Invalid path"));
  }

  @Test
  void testGenerateDifferLTS() {
    var first = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("B", "C", "A"));
    var second = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("B", "C", "A"));
    var differ = WF2SMTDiffGenerator.generateDiffer(first, second, "C");
    assertTrue(differ.firstSubsetOfSecond(10).isEmpty());
    assertTrue(differ.secondSubsetOfFirst(10).isEmpty());
  }
}