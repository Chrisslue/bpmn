package de.monticore.workflow.conformance.conformance.predicate;

import de.monticore.bpmn.conformance.conformance.ctlConformance.PredicateBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.workflow.conformance.AbstractConfTest;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PredicateTest extends AbstractConfTest {

  private final String modelDir = "de.monticore.workflow.conformance.predicate.";
  private WfBuilder builder;

  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }

  static Stream<Arguments> xorSplitSource() {
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
  @MethodSource("xorSplitSource")
  public void testPostPredicateXor(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "post.XOR", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    WfNode res = builder.getWfNode("S");

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.postPredicate(res);

    // then
    Assertions.assertNotNull(predicate);
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("xorSplitSource")
  public void testPrePredicateXor(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "post.XOR", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    WfNode res = builder.getWfNode("S");

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.postPredicate(res);

    // then
    Assertions.assertNotNull(predicate);
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
  @MethodSource("orSource")
  public void testPostPredicateOr(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "post.OR", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.postPredicate(builder.getWfNode("S"));

    // then
    Assertions.assertNotNull(predicate);
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
  @MethodSource("andSource")
  public void testPostPredicateAnd(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "post.AND", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.postPredicate(builder.getWfNode("S"));
    Assertions.assertNotNull(predicate);
    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("andSource")
  public void testPrePredicateAnd(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "pre.AND", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.prePredicate(builder.getWfNode("S"));
    Assertions.assertNotNull(predicate);
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

  @ParameterizedTest
  @MethodSource("complexSource")
  public void testPostPredicateCOMPLEX(List<String> nodeNames, boolean result) {
    // given
    builder = parseAndCreateBuilder(modelDir + "COMPLEX", "");
    List<WfNode> tasks = resolveNodeFormBuilder(nodeNames, builder);

    // when
    Predicate<List<WfNode>> predicate = PredicateBuilder.postPredicate(builder.getWfNode("S"));

    // then
    Assertions.assertNotNull(predicate);
    Assertions.assertEquals(predicate.test(tasks), result);
  }
}
