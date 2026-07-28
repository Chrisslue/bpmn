/*·(c)·https://github.com/MontiCore/monticore·*/
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;


public class SleProjectTest extends AbstractConfTest {

  @Test
  void testTest() {
    init();
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of("target/cd2pojo/test/symbols/"));
    
    loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", true);
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
