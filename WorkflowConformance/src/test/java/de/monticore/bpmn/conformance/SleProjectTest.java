/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class SleProjectTest extends AbstractConfTest {
  
  @Test
  void loadsSymbolsFromCD() {
    init();
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of("target/cd2pojo/test/symbols/de/monticore/bpmn/conformance/sleProject/TimeManagement"));
    
    loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", true);
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  
  @Test
  void failsForIncompleteSymbolsFromCD() {
    init();
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of("target/cd2pojo/test/symbols/de/monticore/bpmn/conformance/sleProject/TimeManagement_Incomplete"));
    
    loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement_Incomplete", true);

    Assertions.assertFalse(Log.getFindings().isEmpty());
  }
  
}
