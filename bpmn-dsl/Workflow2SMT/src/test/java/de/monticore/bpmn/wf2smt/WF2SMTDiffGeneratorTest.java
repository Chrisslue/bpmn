package de.monticore.bpmn.wf2smt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.Resources;
import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class WF2SMTDiffGeneratorTest {

  @BeforeEach
  void setUp() {
    Log.init();
  }

  private final String startName = "Start";
  private final String endName = "End";
  private final String terminatingName = "Term";

  @Test
  void testGenerateDifferModels() {
    var differ =
        WF2SMTDiffGenerator.generateDiffer(
            Resources.SIMPLE, Resources.SIMPLE_EQUIVALENT, startName, endName, terminatingName);
    assertTrue(differ.firstIncludesTracesOfSecond(5).isEmpty());
    assertTrue(differ.secondIncludesTracesOfFirst(5).isEmpty());
    differ =
        WF2SMTDiffGenerator.generateDiffer(
            Resources.SIMPLE, Resources.SIMPLE_NOT_EQUIVALENT, startName, endName, terminatingName);
    assertTrue(differ.firstIncludesTracesOfSecond(5).isEmpty());
    var optWitness = differ.secondIncludesTracesOfFirst(5);
    assertTrue(optWitness.isPresent());
    assertEquals(List.of(startName, endName), optWitness.get());

    Log.setErrorHook(() -> {throw new MCFatalError("msg");});

    // Test invalid model
    assertThrows(
        MCFatalError.class,
        () ->
            WF2SMTDiffGenerator.generateDiffer(
                Resources.SIMPLE, "Invalid path", startName, endName, terminatingName));
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
    assertTrue(differ.firstIncludesTracesOfSecond(10).isEmpty());
    assertTrue(differ.secondIncludesTracesOfFirst(10).isEmpty());
  }

  public static Stream<String> reflexiveArgumentProvider() {
    return Resources.allValidModel().stream()
        .filter(diagram -> !diagram.equals(Resources.NESTED_GATEWAY)) // TODO add when #1 fixed
        .filter(diagram -> !diagram.equals(Resources.MULTIPLE_INCOMING_OUTGOING)); // FIXME
  }

  @ParameterizedTest
  @MethodSource("reflexiveArgumentProvider")
  void testReflexive(String diagramName) {
    var differ =
        WF2SMTDiffGenerator.generateDiffer(
            diagramName, diagramName, startName, endName, terminatingName);
    assertTrue(differ.firstIncludesTracesOfSecond(15).isEmpty());
    assertTrue(differ.secondIncludesTracesOfFirst(15).isEmpty());
  }
}
