package de.monticore.bpmn.cli;

import de.monticore.bpmn.cli.converters.ModelPathTypeConverter;
import de.monticore.io.paths.ModelPath;

/**
 * Registers custom command line converters.
 *
 * @see picocli.CommandLine
 */
public class CommandLine extends picocli.CommandLine {

    public CommandLine(final Object command) {
        super(command);
        registerConverter(ModelPath.class, new ModelPathTypeConverter());
    }

}
