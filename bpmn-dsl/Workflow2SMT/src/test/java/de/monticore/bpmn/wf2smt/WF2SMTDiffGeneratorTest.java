package de.monticore.bpmn.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.Resources;
import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.se_rwth.commons.logging.MCFatalError;
import java.util.List;
import org.junit.jupiter.api.Test;

class WF2SMTDiffGeneratorTest {

  private final String startName = "Start";
  private final String endName = "End";
  private final String terminatingName = "Term";

  @Test
  void testGenerateDifferModels() {
    var differ = WF2SMTDiffGenerator.generateDiffer(Resources.SIMPLE, Resources.SIMPLE_EQUIVALENT, startName, endName,
        terminatingName);
    assertTrue(differ.firstSubsetOfSecond(5).isEmpty());
    assertTrue(differ.secondSubsetOfFirst(5).isEmpty());
    differ = WF2SMTDiffGenerator.generateDiffer(Resources.SIMPLE, Resources.SIMPLE_NOT_EQUIVALENT, startName, endName,
        terminatingName);
    assertTrue(differ.firstSubsetOfSecond(5).isEmpty());
    var optWitness = differ.secondSubsetOfFirst(5);
    assertTrue(optWitness.isPresent());
    assertEquals(List.of(startName, endName), optWitness.get());

    // Test invalid model
    assertThrows(MCFatalError.class,
        () -> WF2SMTDiffGenerator.generateDiffer(Resources.SIMPLE, "Invalid path", startName, endName,
            terminatingName));
  }

  @Test
  void testGenerateDifferLTS() {
    var first = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("B", "C", "A"));
    var second = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("B", "C", "A"));
    var differ = WF2SMTDiffGenerator.generateDiffer(first, second, List.of("C"));
    assertTrue(differ.firstSubsetOfSecond(10).isEmpty());
    assertTrue(differ.secondSubsetOfFirst(10).isEmpty());
  }
}