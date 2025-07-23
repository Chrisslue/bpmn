/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.bpmn.cli.CommandLine;
import de.monticore.bpmn.cli.commands.MainCommand;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class WorkflowCliTest {
  
  // fixme
  @Disabled
  @Test
  void testExport() {
    String[] args = { "-p", "src/test/resources/", "de.monticore.bpmn.CliExportTest.wfm",
        "export" };
    
    int exitCode = new CommandLine(new MainCommand()).execute(args);
    assertEquals(0, exitCode);
  }
  
}
