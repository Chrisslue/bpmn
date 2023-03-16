package de.monticore.bpmn.cli.converters;

import de.monticore.io.paths.MCPath;
import picocli.CommandLine.ITypeConverter;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Custom type converter for {@code ModelPath} parameter.
 */
public class MCPathTypeConverter implements ITypeConverter<MCPath> {

    @Override
    public MCPath convert(final String value) throws Exception {
        Path path = Paths.get(value);
        if (!Files.exists(path)) {
            throw new NoSuchFileException(value);
        }
        return new MCPath(path);
    }

}
