package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.ctl.PredicateGenerator;
import de.monticore.workflow.conformance.utils.BPMNUtils;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PredicateGeneratorTest extends AbstractConfTest {

  private IDWfNodeBuilder builder;

  public void init(String model) {
    super.init();
    String modelDir = "de.monticore.workflow.conformance.predicate.";
    ASTWorkflowCompilationUnit ast = loadModel(modelDir + model);
    builder = BPMNUtils.generateIDWfNode(ast, "");
  }

  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }

  @ParameterizedTest
  @MethodSource("xorSource")
  public void testPostPredicateXor(Set<String> nodeNames, boolean result) {
    // given
    init("XOR");
    Set<IDWfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<Set<IDWfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("orSource")
  public void testPostPredicateOr(Set<String> nodeNames, boolean result) {
    // given
    init("OR");
    Set<IDWfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<Set<IDWfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("andSource")
  public void testPostPredicateAnd(Set<String> nodeNames, boolean result) {
    // given
    init("AND");
    Set<IDWfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<Set<IDWfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("andSource")
  public void testPostPredicateCOMPLEX(Set<String> nodeNames, boolean result) {
    // given
    init("COMPLEX");
    Set<IDWfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<Set<IDWfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> orSource() {
    return Stream.of(
        Arguments.of(Set.of("Task1"), false),
        Arguments.of(Set.of("Task2"), false),
        Arguments.of(Set.of("Task1", "Task2"), false),
        Arguments.of(Set.of("Task1", "Start"), false),
        Arguments.of(Set.of("End", "Start"), false),
        Arguments.of(Set.of("Task1", "End"), false),
        Arguments.of(Set.of("Task2", "End"), false),
        Arguments.of(Set.of("Start", "Task2"), false),
        Arguments.of(Set.of("End", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Task2", "Start"), true),
        Arguments.of(Set.of("End", "Task1", "Start"), true),
        Arguments.of(Set.of("End", "Task1", "Start", "OrSplit"), true),
        Arguments.of(Set.of("End", "Task2", "Start"), true));
  }

  static Stream<Arguments> xorSource() {
    return Stream.of(
        Arguments.of(Set.of("Task1"), false),
        Arguments.of(Set.of("Task2"), false),
        Arguments.of(Set.of("Task1", "Task2"), false),
        Arguments.of(Set.of("Task1", "Start"), false),
        Arguments.of(Set.of("Task1", "End"), false),
        Arguments.of(Set.of("Start", "Task2"), false),
        Arguments.of(Set.of("End", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Task2", "Start"), false),
        Arguments.of(Set.of("End", "Task1", "Start"), true),
        Arguments.of(Set.of("End", "Task1", "Start", "XorSplit"), true),
        Arguments.of(Set.of("End", "Task2", "Start"), true));
  }

  static Stream<Arguments> andSource() {
    return Stream.of(
        Arguments.of(Set.of("Task1"), false),
        Arguments.of(Set.of("Task2"), false),
        Arguments.of(Set.of("Task1", "Task2"), false),
        Arguments.of(Set.of("Task1", "Start"), false),
        Arguments.of(Set.of("Task1", "End"), false),
        Arguments.of(Set.of("Start", "Task2"), false),
        Arguments.of(Set.of("End", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Task2"), false),
        Arguments.of(Set.of("End", "Task1", "Start"), false),
        Arguments.of(Set.of("End", "Task2", "Start"), false),
        Arguments.of(Set.of("End", "Task1", "Task2", "Start"), true),
        Arguments.of(Set.of("End", "Task1", "Task2", "Start", "AndSplit"), true));
  }

  public Set<IDWfNode> resolveNode(Set<String> nodeNames) {
    Set<IDWfNode> res = new HashSet<>();

    for (String name : nodeNames) {
      Assertions.assertTrue(builder.getNode(name).isPresent());
      IDWfNode node = builder.getNode(name).get();
      res.add(node);
    }
    return res;
  }
}
