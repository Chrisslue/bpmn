package de.monticore.bpmn.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/** Util for writing to files. */
public class FilePrinter {

  // content to write
  private String from;

  private FilePrinter(final String from) {
    this.from = from;
  }

  public static FilePrinter from(final String content) {
    return new FilePrinter(content);
  }

  public File to(final Path path, final String name, final String extension) throws IOException {
    final File file = FileUtils.createFile(path, name, extension);
    FileUtils.writeToFile(file, from);

    return file;
  }

  public File to(final Path path) throws IOException {
    final File file = FileUtils.createFile(path);
    FileUtils.writeToFile(file, from);

    return file;
  }
}
