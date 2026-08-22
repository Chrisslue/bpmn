/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.sleProject;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class SleProjectTest extends AbstractTest {
  
  @Test
  void loadsSymbolsFromCD() {
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
        "target/cd2pojo/test/symbols/de/monticore/bpmn/sleProject/TimeManagement"));
    
    loadModel("de.monticore.bpmn.sleProject.TimeManagement");
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  void failsForIncompleteSymbolsFromCD() {
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
        "target/cd2pojo/test/symbols/de/monticore/bpmn/sleProject/TimeManagementIncomplete"));
    
    loadModel("de.monticore.bpmn.sleProject.TimeManagementIncomplete");
    
    List<Finding> errors = Log.getFindings().stream().filter(Finding::isError).toList();
    
    Assertions.assertEquals(1, errors.size());
    Assertions.assertEquals("0xA0324 Cannot find symbol TimeSlotList", errors.getFirst().getMsg());
  }
  
}
