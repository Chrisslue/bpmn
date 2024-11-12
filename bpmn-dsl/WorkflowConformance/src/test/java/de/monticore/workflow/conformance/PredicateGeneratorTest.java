package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.conformance.PredicateGenerator;
import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.datastructure.IDWfNodeBuilder;
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

class PredicateGeneratorTest extends AbstractConfTest {

  private IDWfNodeBuilder builder;

  public void init(String model) {
    super.init();
    String modelDir = "de.monticore.workflow.conformance.predicate.";
    ASTWorkflowCompilationUnit ast = loadModel(modelDir + model);
    builder = BPMNUtils.generateIDWfNode(ast, i->i);
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

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("orSource")
  public void testPostPredicateOr(List<String> nodeNames, boolean result) {
    // given
    init("OR");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("andSource")
  public void testPostPredicateAnd(List<String> nodeNames, boolean result) {
    // given
    init("AND");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("complexSource")
  public void testPostPredicateCOMPLEX(List<String> nodeNames, boolean result) {
    // given
    init("COMPLEX");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  @ParameterizedTest
  @MethodSource("concreteSource")
  public void testPostPredicateConcrete(List<String> nodeNames, boolean result) {
    // given
    init("Concrete");
    List<WfNode> tasks = resolveNode(nodeNames);

    // when
    Predicate<List<WfNode>> predicate = PredicateGenerator.postPredicate(builder.build());

    // then
    Assertions.assertEquals(predicate.test(tasks), result);
  }

  static Stream<Arguments> concreteSource() {
    return Stream.of(
        Arguments.of(List.of("Task1"), false),
        Arguments.of(List.of("Task1", "Start"), false),
        Arguments.of(List.of("End", "Start"), false),
        Arguments.of(List.of("Task1", "End"), false),
        Arguments.of(List.of("End", "Task1", "Start"), true));
  }

  static Stream<Arguments> orSource() {
    return Stream.of(
        Arguments.of(List.of("Task1"), false),
        Arguments.of(List.of("Task2"), false),
        Arguments.of(List.of("Task1", "Task2"), false),
        Arguments.of(List.of("Task1", "Start"), false),
        Arguments.of(List.of("End", "Start"), false),
        Arguments.of(List.of("Task1", "End"), false),
        Arguments.of(List.of("Task2", "End"), false),
        Arguments.of(List.of("Start", "Task2"), false),
        Arguments.of(List.of("End", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Task2", "Start"), true),
        Arguments.of(List.of("End", "Task1", "Start"), true),
        Arguments.of(List.of("End", "Task1", "Start", "OrSplit"), true),
        Arguments.of(List.of("End", "Task2", "Start"), true));
  }

  static Stream<Arguments> xorSource() {
    return Stream.of(
        Arguments.of(List.of("Task1"), false),
        Arguments.of(List.of("Task2"), false),
        Arguments.of(List.of("Task1", "Task2"), false),
        Arguments.of(List.of("Task1", "Start"), false),
        Arguments.of(List.of("Task1", "End"), false),
        Arguments.of(List.of("Start", "Task2"), false),
        Arguments.of(List.of("End", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Task2", "Start"), false),
        Arguments.of(List.of("End", "Task1", "Start"), true),
        Arguments.of(List.of("End", "Task1", "Start", "XorSplit"), true),
        Arguments.of(List.of("End", "Task2", "Start"), true));
  }

  static Stream<Arguments> andSource() {
    return Stream.of(
        Arguments.of(List.of("Task1"), false),
        Arguments.of(List.of("Task2"), false),
        Arguments.of(List.of("Task1", "Task2"), false),
        Arguments.of(List.of("Task1", "Start"), false),
        Arguments.of(List.of("Task1", "End"), false),
        Arguments.of(List.of("Start", "Task2"), false),
        Arguments.of(List.of("End", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Task2"), false),
        Arguments.of(List.of("End", "Task1", "Start"), false),
        Arguments.of(List.of("End", "Task2", "Start"), false),
        Arguments.of(List.of("End", "Task1", "Task2", "Start"), true),
        Arguments.of(List.of("End", "Task1", "Task2", "Start", "AndSplit"), true));
  }

  static Stream<Arguments> complexSource() {
    return Stream.of(
            Arguments.of(List.of("Task1"), false),
            Arguments.of(List.of("Task2"), false),
            Arguments.of(List.of("Task1", "Task2"), false),
            Arguments.of(List.of("Task1", "Start"), false),
            Arguments.of(List.of("Task1", "End"), false),
            Arguments.of(List.of("Start", "Task2"), false),
            Arguments.of(List.of("End", "Task2"), false),
            Arguments.of(List.of("End", "Task1", "Task2"), false),
            Arguments.of(List.of("End", "Task1", "Start"), false),
            Arguments.of(List.of("End", "Task2", "Start"), false),
            Arguments.of(List.of("End", "Task1", "Task2", "Start"), false),
            Arguments.of(List.of("End", "Task1", "Task2", "Start", "AndSplit"), false),
            Arguments.of(List.of("End", "Task1", "Task3","Task4","Task5","Task6", "Start"), true),
            Arguments.of(List.of("End", "Task2", "Task3","Task4","Task5","Task6", "Start"), true)
    );

  }

  public List<WfNode> resolveNode(List<String> nodeNames) {
    List<WfNode> res = new ArrayList<>();

    for (String name : nodeNames) {
      Assertions.assertTrue(builder.getNode(name).isPresent());
      IDWfNode node = builder.getNode(name).get();
      res.add(node);
    }
    return res;
  }
}
