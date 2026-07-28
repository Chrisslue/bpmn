/*·(c)·https://github.com/MontiCore/monticore·*/
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;


public class sleProjectTest extends AbstractConfTest {

  @Test
  void testTest() {
    init();
    WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of("target/cd2pojo/test/symbols/"));
    var astroot = loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", true);
  }
}
