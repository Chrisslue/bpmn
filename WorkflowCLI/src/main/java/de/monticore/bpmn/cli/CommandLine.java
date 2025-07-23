/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cli;

import de.monticore.bpmn.cli.converters.MCPathTypeConverter;
import de.monticore.io.paths.MCPath;

/**
 * Registers custom command line converters.
 *
 * @see picocli.CommandLine
 */
public class CommandLine extends picocli.CommandLine {
  
  public CommandLine(final Object command) {
    super(command);
    registerConverter(MCPath.class, new MCPathTypeConverter());
  }
  
}
