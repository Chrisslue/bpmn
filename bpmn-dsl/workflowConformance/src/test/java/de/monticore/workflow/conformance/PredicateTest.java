package de.monticore.workflow.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGenerator;
import de.monticore.workflow.conformance.datastructure.ctl.PredicateGenerator;
import de.se_rwth.commons.logging.Log;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PredicateTest extends AbstractConfTest {

  IdWfNode s;
  IdWfNode t1;
  IdWfNode e;
  ASTWorkflowCompilationUnit con;
  IdWfNode graph;

  public void initActivity() {

    con = loadModel("de.monticore.workflow.conformance.predicate.CTL");
    CTLGenerator generator = new CTLGenerator();

    graph = generator.generateNode(con, "");

    Assertions.assertTrue(IdWfNode.getNode("Start").isPresent());
    s = IdWfNode.getNode("Start").get();

    Assertions.assertTrue(IdWfNode.getNode("Task1").isPresent());
    t1 = IdWfNode.getNode("Task1").get();

    Assertions.assertTrue(IdWfNode.getNode("End").isPresent());
    e = IdWfNode.getNode("End").get();
  }

  @BeforeEach
  public void setup() {
    init();
    Log.init();

    initActivity();
  }

  @Test
  public void testPredicateGenerator() {

    PredicateGenerator gen = new PredicateGenerator();

    Predicate<Set<IdWfNode>> predicate = gen.postPredicate(graph);

    Assertions.assertFalse(predicate.test(Set.of(s)));
    Assertions.assertFalse(predicate.test(Set.of(s, t1)));
    Assertions.assertTrue(predicate.test(Set.of(s, t1, e)));
  }
}
