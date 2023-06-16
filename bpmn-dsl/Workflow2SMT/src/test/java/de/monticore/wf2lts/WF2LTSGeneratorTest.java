package de.monticore.wf2lts;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WF2LTSGeneratorTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().init();
    BasicSymbolsMill.initializePrimitives();
  }

  private static String diagramFile = "src/test/resources/de/monticore/wf2smt/Prototype.wfm";

  @Test
  public void testLoading() {
    WF2LTSGenerator.loadBPMN(diagramFile);
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testComplete() {
    var lts = WF2LTSGenerator.ltsOfWorkflow(diagramFile);
    System.out.println(lts.toMermaid().build());
    Assertions.assertEquals(1, lts.getOutgoings(lts.getStart()).size());
    Assertions.assertEquals("Start", lts.getOutgoings(lts.getStart()).get(0).getLabel());

  }

}
