package de.monticore.workflow.conformance.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.AbstractConfTest;
import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.datastructure.interf.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PredicateTest extends AbstractConfTest {

  private IDWfNodeBuilder builder;

  public void init(String model) {
    super.init();
    String modelDir = "de.monticore.workflow.conformance.predicate.";
    ASTWorkflowCompilationUnit ast = loadModel(modelDir + model);
    builder = BPMNUtils.generateIDWfNode(ast, i -> i);
  }

  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }

  @ParameterizedTest
  @MethodSource("xorSource")
  public void testPostPredicateXor(List<String> nodeNames, boolean result) {
    // given
    init("XOR");
    List<WfNode> tasks = resolveNode(nodeNames);

    WfNode res = builder.getNode("S");

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(res);

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> xorSource() {
    return Stream.of(
        Arguments.of(List.of("T1"), true),
        Arguments.of(List.of("T2"), true),
        Arguments.of(List.of("T3"), true),
        Arguments.of(List.of("T4"), true),
        Arguments.of(List.of("T1", "S"), true),
        Arguments.of(List.of("S", "T2"), true),
        Arguments.of(List.of("S", "T2"), true),
        Arguments.of(List.of("T1", "T2"), false),
        Arguments.of(List.of("T1", "T2", "S"), false),
        Arguments.of(List.of("T1", "S"), true),
        Arguments.of(List.of("T1", "T3", "XOR1"), false),
        Arguments.of(List.of("T2", "S"), true));
  }

  @ParameterizedTest
  @MethodSource("orSource")
  public void testPostPredicateOr(List<String> nodeNames, boolean result) {
    // given
    init("OR");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.getNode("S"));

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> orSource() {
    return Stream.of(
        Arguments.of(List.of("T1"), true),
        Arguments.of(List.of("T2"), true),
        Arguments.of(List.of("T1", "T2"), true),
        Arguments.of(List.of("T1", "S"), true),
        Arguments.of(List.of("S"), false),
        Arguments.of(List.of("T2", "S"), true),
        Arguments.of(List.of("T2", "T3"), true),
        Arguments.of(List.of("T2", "T2", "T3", "T4"), true),
        Arguments.of(List.of("T3", "T4"), true),
        Arguments.of(List.of("T1", "T3", "T4"), true),
        Arguments.of(List.of("T2", "T3", "T4"), true),
        Arguments.of(List.of("T2", "T4"), true));
  }

  @ParameterizedTest
  @MethodSource("andSource")
  public void testPostPredicateAnd(List<String> nodeNames, boolean result) {
    // given
    init("AND");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.getNode("S"));

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> andSource() {
    return Stream.of(
        Arguments.of(List.of("T1"), false),
        Arguments.of(List.of("T2"), false),
        Arguments.of(List.of("T1", "T2"), false),
        Arguments.of(List.of("T1", "S"), false),
        Arguments.of(List.of("S", "T2"), false),
        Arguments.of(List.of("T2"), false),
        Arguments.of(List.of("T1", "T2"), false),
        Arguments.of(List.of("T1", "S"), false),
        Arguments.of(List.of("T2", "S"), false),
        Arguments.of(List.of("T1", "T2", "T3", "T4"), true),
        Arguments.of(List.of("T1", "T2", "T3", "T4", "S"), true));
  }

  @ParameterizedTest
  @MethodSource("complexSource")
  public void testPostPredicateCOMPLEX(List<String> nodeNames, boolean result) {
    // given
    init("COMPLEX");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.getNode("S"));

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> complexSource() {
    return Stream.of(
        Arguments.of(List.of("T1"), true),
        Arguments.of(List.of("T2"), true),
        Arguments.of(List.of("T4"), true),
        Arguments.of(List.of("T4", "T2"), true),
        Arguments.of(List.of("T5", "T3"), true),
        Arguments.of(List.of("T1", "T5", "T3"), false),
        Arguments.of(List.of("T4", "T5", "T3"), false),
        Arguments.of(List.of("T1", "T4"), false));
  }

  public List<WfNode> resolveNode(List<String> nodeNames) {
    List<WfNode> res = new ArrayList<>();

    for (String name : nodeNames) {
      builder.getNode(name);
      IDWfNode node = builder.getNode(name);
      res.add(node);
    }
    return res;
  }
}
