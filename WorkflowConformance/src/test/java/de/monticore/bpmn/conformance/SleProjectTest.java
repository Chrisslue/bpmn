/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class SleProjectTest extends AbstractConfTest {
  
  @Test
  void loadsSymbolsFromCD() {
    init();
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
        "target/cd2pojo/test/symbols/de/monticore/bpmn/conformance/sleProject/TimeManagement"));
    
    loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", true);
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  void failsForIncompleteSymbolsFromCD() {
    init();
    
    // We have to start the Logger in interactive mode. Otherwise, it terminates the whole
    // program if it has to log an error which negates the purpose of this test
    Log.enableFailQuick(false);
    
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
        "target/cd2pojo/test/symbols/de/monticore/bpmn/conformance/sleProject/TimeManagementIncomplete"));
    
    loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagementIncomplete", true);
    
    List<Finding> errors = Log.getFindings().stream().filter(Finding::isError).toList();
    
    Assertions.assertEquals(1, errors.size());
    Assertions.assertEquals("0xA0324 Cannot find symbol TimeSlotList", errors.getFirst().getMsg());
  }
  
}
