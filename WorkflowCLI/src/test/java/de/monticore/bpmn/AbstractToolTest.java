/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractToolTest {
  
  protected static final String MODEL_DIR = "src/test/resources/de/monticore/bpmn/";
  protected static final String SYMBOL_DIR = "src/test/resources/de/monticore/bpmn/";
  
  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    WorkflowMill.init();
    WorkflowMill.globalScope().clear();
    WorkflowMill.globalScope().setSymbolPath(new MCPath(SYMBOL_DIR));
    BasicSymbolsMill.initializePrimitives();
  }
  
  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
  }
  
}
