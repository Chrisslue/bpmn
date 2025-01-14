package de.monticore.bpmn.utils;

import de.se_rwth.commons.Joiners;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.zeroturnaround.exec.ProcessExecutor;

/** Interface for GraphViz. */
public class GraphVizWriter {

  private static Optional<Boolean> available = Optional.empty();
  private Path outputDir;
  private File dotFile;
  private String fileName;
  private int timeout = 3;

  /**
   * Checks if GraphViz is installed and available in the Path
   *
   * @return
   */
  public static boolean isAvailable() {
    if (!available.isPresent()) {
      try {
        // if GraphViz is installed, exit code should be 0
        boolean test = 0 == new ProcessExecutor().command("dot", "-V").execute().getExitValue();
        available = Optional.of(test);
      } catch (final Exception ignored) {
      }
    }
    return available.orElse(false);
  }

  /**
   * Sets the input file containing the DOT graph to be printed.
   *
   * @param dotFile the file containing the graph in the DOT format
   * @return this
   */
  public GraphVizWriter input(final File dotFile) {
    this.dotFile = dotFile;
    return this;
  }

  /**
   * Sets the output directory.
   *
   * @param outputDir the output directory
   * @return this
   */
  public GraphVizWriter outputDir(final Path outputDir) {
    this.outputDir = outputDir;
    return this;
  }

  /**
   * Sets the name of the output file.
   *
   * @param fileName the name of the output file
   * @return this
   */
  public GraphVizWriter outputName(final String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * Sets a timeout. When this timeout is reached, the system process executing GraphViz is
   * terminated.
   *
   * @param seconds the value for the timeout in seconds
   * @return this
   */
  public GraphVizWriter timeout(final int seconds) {
    this.timeout = seconds;
    return this;
  }

  /**
   * Executes GraphViz.
   *
   * @param format "png" xor "svg"
   * @return this
   * @throws IOException
   */
  private GraphVizWriter generate(final OutputFormat format) throws IOException {
    final Path imagePath = outputDir.resolve(Joiners.DOT.join(fileName, format.get()));
    final File imageFile = FileUtils.createFile(imagePath);

    final String fileTypeArgument = "-T" + format.get();
    try {
      new ProcessExecutor()
          .command(
              "dot", fileTypeArgument, dotFile.getAbsolutePath(), "-o", imageFile.getAbsolutePath())
          .destroyOnExit()
          .timeout(timeout, TimeUnit.SECONDS)
          .execute();
    } catch (final Exception ignored) {
    }

    return this;
  }

  /**
   * Generates a SVG image by executing GraphViz.
   *
   * @return this
   * @throws IOException
   */
  public GraphVizWriter generateSvg() throws IOException {
    return this.generate(OutputFormat.SVG);
  }

  /**
   * Generates a PNG image by executing GraphViz.
   *
   * @return this
   * @throws IOException
   */
  public GraphVizWriter generatePng() throws IOException {
    return this.generate(OutputFormat.PNG);
  }

  /** The output format. */
  private enum OutputFormat {
    SVG("svg"),
    PNG("png");

    final String format;

    OutputFormat(final String format) {
      this.format = format;
    }

    String get() {
      return format;
    }
  }
}
