package de.monticore.bpmn;

import de.monticore.bpmn.cli.CommandLine;
import de.monticore.bpmn.cli.commands.MainCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkflowCliTest {

    @Test
    void testExport() {
        String[] args = {"-p", "src/test/resources/", "de.monticore.bpmn.CliExportTest", "export"};

        int exitCode = new CommandLine(new MainCommand()).execute(args);
        assertEquals(0, exitCode);
    }

}
