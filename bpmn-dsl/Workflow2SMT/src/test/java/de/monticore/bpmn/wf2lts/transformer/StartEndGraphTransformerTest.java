package de.monticore.bpmn.wf2lts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.bpmn.Resources;
import de.monticore.bpmn.wf2lts.LTSTestingUtils;
import de.monticore.bpmn.wf2lts.UniqueStartAndEndEventNaming;
import de.monticore.bpmn.wf2lts.WF2LTSGenerator;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StartEndGraphTransformerTest {

  @BeforeEach
  void setUp() {
    Log.init();
  }

  @Test
  void testPrototype() {
    var ast = WF2LTSGenerator.loadBPMN(Resources.PROTOTYPE);
    var graphWithScopes = WF2LTSGenerator.transformToGraph(ast);
    var naming = new UniqueStartAndEndEventNaming("Start", "End", "Term");
    var transformer = new StartEndGraphTransformer(
        "_S", "_E",
        new UniqueStartAndEndEventNaming("Start", "End", "Term"),
        new DefaultGatewayTransformer(new DefaultGatewayInterleaving(), naming),
        new DefaultSubprocessTransformer()
    );
    var lts = transformer.transform(graphWithScopes);

    // Assert no unreachable states.
    var allStates = lts.getStates();
    new LTSTraverser(lts).depthFirstSearchLTS(lts.getStart(), allStates::remove);
    Assertions.assertTrue(allStates.isEmpty(), "LTS has unreachable states");

    assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());
    var upperBranch = List.of("A_S", "A_E", "T_Start", "G_S", "G_E", "T_End");
    var validPaths = new ArrayList<List<String>>();
    // B can occur at any point especially in between start and end of another task.
    for (int i = 0; i < upperBranch.size(); i++) {
      for (int j = i + 1; j < upperBranch.size(); j++) {
        var permutation = new ArrayList<>(upperBranch);
        permutation.add(i, "B_S");
        permutation.add(j + 1, "B_E"); // +1 as we inserted "G_S" some place before
        // Add start and end
        permutation.add(0, "Start");
        permutation.add("End");
        validPaths.add(permutation);
      }
    }
    LTSTestingUtils.assertPathsExist(lts, validPaths);
  }
}