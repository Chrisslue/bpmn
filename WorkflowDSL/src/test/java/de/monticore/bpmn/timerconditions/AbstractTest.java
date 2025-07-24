/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractTest {
  
  protected WorkflowParser parser = WorkflowMill.parser();
  
  @BeforeAll
  public static void init() {
    // replacing log by a side effect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
  }
  
}
