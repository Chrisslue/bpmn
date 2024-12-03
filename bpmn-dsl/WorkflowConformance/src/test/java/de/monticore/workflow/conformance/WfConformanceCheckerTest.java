package de.monticore.workflow.conformance;

import static de.monticore.bpmn.conformance.datastructures.utils.CheckResult.Result.CONFORM;
import static de.monticore.bpmn.conformance.datastructures.utils.CheckResult.Result.NON_CONFORM;

import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import de.se_rwth.commons.logging.Log;
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
        Arguments.of("NonConform1", false, Map.of("S", NON_CONFORM)));
  }

  @BeforeEach
  public void setup() {
    init();
    Log.initDEBUG();
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
    Assertions.assertEquals( expected  ,currentResult);

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

  @Test
  public void basicConformanceCheckerTest() {
    boolean res = checkConformance("Concrete", "Reference");
    Assertions.assertTrue(res);
  }

  public boolean checkConformance(String con, String ref) {

    concrete = loadModel(modelDir + con);
    reference = loadModel(modelDir + ref);
    return new WfConformanceChecker().checkConformance(concrete, reference, "ref");
  }
}
