package de.monticore.bpmn;

import de.monticore.bpmn.cli.CommandLine;
import de.monticore.bpmn.cli.commands.MainCommand;

/**
 * Command Line Interface fro BPMN language.
 */
public class WorkflowCli {

    public static void main(final String[] args) {
        int exitCode = new CommandLine(new MainCommand()).execute(args);
        System.exit(exitCode);
    }

}
