package de.monticore.bpmn.cli.commands;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import java.nio.file.NoSuchFileException;
import picocli.CommandLine;

/**
 * CLI options shared by multiple commands.
 *
 * @see CheckModelCommand
 * @see GenerateXmlCommand
 */
class CommonCliOptions {

  String qualifiedModel;

  @CommandLine.Option(
      names = {"-mp", "--model-path"},
      paramLabel = "<DIR>",
      required = true)
  MCPath modelPath;

  @CommandLine.Parameters(
      paramLabel = "<FILE>",
      description = "Qualified name of workflow model to process.")
  private void setQualifiedModel(final String qualifiedModelName) throws NoSuchFileException {
    if (modelPath.find(Names.getPathFromPackage(qualifiedModelName)).isPresent()) {
      qualifiedModel = qualifiedModelName;
    } else {
      throw new NoSuchFileException(qualifiedModelName);
    }
  }

  @CommandLine.Option(names = {"-v", "--verbose"})
  boolean verbose;
}
