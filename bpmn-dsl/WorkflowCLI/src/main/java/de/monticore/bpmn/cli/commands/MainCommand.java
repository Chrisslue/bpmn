package de.monticore.bpmn.cli.commands;

import de.monticore.bpmn.cli.VersionProvider;
import de.monticore.bpmn.lang.Import;
import de.monticore.bpmn.utils.ModelUtils;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.nio.file.NoSuchFileException;

/**
 * The main command. Provides the sub-commands {@code verify} and {@code export}.
 *
 * @see CheckModelCommand
 * @see GenerateXmlCommand
 */
@Command(name = "wftool",
        versionProvider = VersionProvider.class,
        synopsisSubcommandLabel = "COMMAND",
        subcommands = {CheckModelCommand.class, GenerateXmlCommand.class},
        mixinStandardHelpOptions = true,
        header = {
                "@|green  _      __         __    _____             ______          __|@",
                "@|green | | /| / /__  ____/ /__ / _/ /__ _    __  /_  __/__  ___  / /|@",
                "@|green | |/ |/ / _ \\/ __/  '_// _/ / _ \\ |/|/ /   / / / _ \\/ _ \\/ /|@",
                "@|green |__/|__/\\___/_/ /_/\\_\\/_//_/\\___/__,__/   /_/  \\___/\\___/_/|@",
                ""}
)
public class MainCommand implements Runnable {

    static final Import OCL_DEFAULT_TYPES_IMPORT = new Import("de.monticore.workflow._types.ocl.DefaultTypes", true);

    @CommandLine.Spec
    private
    CommandLine.Model.CommandSpec spec;

    ModelCoordinate qualifiedModel;

    @CommandLine.Option(
            names = {"-p", "--model-path"},
            paramLabel = "DIR",
            description = "Path to model directory.",
            required = true
    )
    ModelPath modelPath;

    @CommandLine.Parameters(
            paramLabel = "FILE",
            description = "Qualified name of BPMN input model."
    )
    private void setQualifiedModel(final String qualifiedModelName) throws NoSuchFileException {
        ModelCoordinate modelCoordinate = ModelUtils.getCoordinate(modelPath, qualifiedModelName);
        if (!modelCoordinate.exists()) {
            throw new NoSuchFileException(modelCoordinate.getQualifiedPath().toString());
        }
        qualifiedModel = modelCoordinate;
    }

    @CommandLine.Option(names = {"-v", "--verbose"})
    boolean verbose;

    public void run() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new MainCommand()).execute(args));
    }

}
