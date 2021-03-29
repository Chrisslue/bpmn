package de.monticore.bpmn.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import de.se_rwth.commons.Joiners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Utilities for creating files.
 */
public class FileUtils {

    public static File createFile(final Path path, final String name, final String extension) throws IOException {
        return createFile(path.resolve(Joiners.DOT.join(name, extension)));
    }

    public static File createFile(final Path path) throws IOException {
        final File file = path.toFile();
        Files.createParentDirs(file);
        Files.touch(file);

        return file;
    }

    public static File createTempFile(final String prefix, final String extension) throws IOException {
        final File file = File.createTempFile(prefix, "." + extension);
        file.deleteOnExit();

        return file;
    }

    public static void writeToFile(final File file, final String content) throws IOException {
        Files.asCharSink(file, Charsets.UTF_8).write(content);
    }

}
