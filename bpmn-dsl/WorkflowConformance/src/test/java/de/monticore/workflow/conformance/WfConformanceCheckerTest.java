package de.monticore.workflow.conformance;

import static de.monticore.bpmn.conformance.datastructures.utils.CheckResult.Result.*;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WfConformanceCheckerTest extends AbstractConfTest {
  private ASTWorkflowCompilationUnit concrete;
  private ASTWorkflowCompilationUnit reference;

  private final String modelDir = "de.monticore.workflow.conformance.conformance.";

  public static Stream<Arguments> xorConformance() {
    return Stream.of(
        Arguments.of("Conform1", true, Map.of("T1", CONFORM, "S", CONFORM)),
        Arguments.of("Conform2", true, Map.of("T1", CONFORM, "T2", CONFORM, "S", CONFORM)),
        Arguments.of("NonConform1", false, Map.of("S", NON_CONFORM)),
        Arguments.of(
            "NonConform2",
            false,
            Map.of("T1", CONFORM, "T2", CONFORM, "T3", CONFORM, "S", NON_CONFORM)));
  }

  @BeforeEach
  public void setup() {
    init();
    // Log.initDEBUG();
  }

  @ParameterizedTest
  @MethodSource("xorConformance")
  public void testConformanceWithXorGateway(
      String input, boolean expected, Map<String, CheckResult.Result> nodes) {

    // given
    reference = loadModel(modelDir + "xor.Reference");
    concrete = loadModel(modelDir + "xor." + input);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    // Then
    Assertions.assertEquals(expected, currentResult);

    for (var node : nodes.entrySet()) {
      var currentNode = getResultOfNode(node.getKey(), checker.getCheckResult());
      Assertions.assertEquals(node.getValue(), currentNode);
    }
  }

  @Test
  public void test() {

    var d = xorConformance().collect(Collectors.toList()).get(2);
    Object[] params = d.get();

    // Extract arguments based on index
    String name = (String) params[0]; // Index 0
    boolean expectedResult = (boolean) params[1]; // Index 1
    Map<String, CheckResult.Result> conformanceMap =
        (Map<String, CheckResult.Result>) params[2]; // Index 2

    // Call the function with the extracted arguments
    check(name, expectedResult, conformanceMap);
  }

  public void check(String input, boolean expected, Map<String, CheckResult.Result> nodes) {

    // given
    reference = loadModel(modelDir + "xor.Reference");
    concrete = loadModel(modelDir + "xor." + input);

    // when
    WfConformanceChecker checker = new WfConformanceChecker();
    boolean currentResult = checker.checkConformance(concrete, reference, "ref");

    // Then
    Assertions.assertEquals(expected, currentResult);

    for (var node : nodes.entrySet()) {
      var currentNode = getResultOfNode(node.getKey(), checker.getCheckResult());
      Assertions.assertEquals(node.getValue(), currentNode);
    }
  }

  public CheckResult.Result getResultOfNode(String node, Set<CheckResult> results) {
    var res = results.stream().filter(n -> n.getNode().getLabel().equals(node)).findAny();
    Assertions.assertTrue(res.isPresent());
    return res.get().getResult();
  }
}
