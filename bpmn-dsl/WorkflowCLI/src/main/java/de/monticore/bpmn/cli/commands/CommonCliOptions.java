package de.monticore.bpmn.cli.commands;

import de.monticore.bpmn.utils.ModelUtils;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelPath;
import picocli.CommandLine;

import java.nio.file.NoSuchFileException;

/**
 * CLI options shared by multiple commands.
 *
 * @see CheckModelCommand
 * @see GenerateXmlCommand
 */
class CommonCliOptions {

    ModelCoordinate qualifiedModel;

    @CommandLine.Option(names = {"-mp", "--model-path"}, paramLabel = "<DIR>", required = true)
    ModelPath modelPath;

    @CommandLine.Parameters(paramLabel = "<FILE>", description = "Qualified name of workflow model to process.")
    private void setQualifiedModel(final String qualifiedModelName) throws NoSuchFileException {
        ModelCoordinate modelCoordinate = ModelUtils.getCoordinate(modelPath, qualifiedModelName);
        if (!modelCoordinate.exists()) {
            throw new NoSuchFileException(modelCoordinate.getQualifiedPath().toString());
        }
        qualifiedModel = modelCoordinate;
    }

    @CommandLine.Option(names = {"-v", "--verbose"})
    boolean verbose;

}
