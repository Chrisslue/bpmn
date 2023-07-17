package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.lts.LTS2Mermaid;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.util.Objects;
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

  private static String diagramFile = Objects.requireNonNull(WF2LTSGenerator.class.getResource("../Prototype.wfm"))
      .getPath();

  @Test
  public void testLoading() {
    WF2LTSGenerator.loadBPMN(diagramFile);
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testComplete() {

    var lts = WF2LTSGenerator.workflow2LTS(diagramFile);
    System.out.println(lts.toModel(new LTS2Mermaid()).build());
    Assertions.assertEquals(1, lts.getOutgoings(lts.getStart()).size());
    Assertions.assertEquals("Start", lts.getOutgoings(lts.getStart()).get(0).getLabel());
  }
}
