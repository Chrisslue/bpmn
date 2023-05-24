package de.monticore.wf2ltl;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WF2LTLGeneratorTest {

  @BeforeEach
  public void init() {
    Log.init();
    Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().init();
    BasicSymbolsMill.initializePrimitives();
  }

  @Test
  public void testLoading() {
    WF2LTLGenerator.loadBPMN("src/test/resources/de/monticore/wf2smt/Prototype.wfm");
    Assertions.assertEquals(0, Log.getErrorCount());
  }
}
