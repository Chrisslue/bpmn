package de.monticore.bpmn.analysis.lola;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * Interface for Low Level Petri Net Analyzer (LoLA) model checker.
 *
 * <p>http://service-technology.org/lola/
 */
public class LoLaChecker {

  private static Optional<Boolean> available = Optional.empty();

  private List<String> arguments = Lists.newArrayList();

  private File input;

  private int timeout = 10; // seconds

  private LoLaResult result;

  /**
   * Checks if LoLA is installed and available in the Path
   *
   * @return
   */
  public static boolean isAvailable() {
    if (!available.isPresent()) {
      try {
        // if LoLa is installed, exit code should be 0
        boolean test = 0 == new ProcessExecutor().command("lola", "-h").execute().getExitValue();
        available = Optional.of(test);
      } catch (final Exception ignored) {
      }
    }
    return available.orElse(false);
  }

  /**
   * Adds a command line parameter to be passed to LoLA.
   *
   * @param key the key of the command line parameter
   * @return this
   */
  public LoLaChecker parameter(final String key) {
    arguments.add("--" + key);
    return this;
  }

  /**
   * Adds a key-value command line parameter to be passed to LoLA.
   *
   * @param key the key of the command line parameter
   * @param value the value of the command line parameter
   * @return this
   */
  public LoLaChecker parameter(final String key, final String value) {
    arguments.add("--" + key + "=" + value);
    return this;
  }

  /**
   * Sets the CTL* formula to be checked.
   *
   * @see LoLaFormulae
   * @param formula the CTL* formula
   * @return this
   */
  public LoLaChecker formula(final String formula) {
    return parameter("formula", formula); // "\"" + formula + "\"");
  }

  /**
   * Sets the input file containing the Petri net to be checked. The Petri net must be in the LoLA
   * format.
   *
   * @param input the file containing the Petri net in the LoLA format
   * @return this
   */
  public LoLaChecker input(final File input) {
    this.input = input;
    return this;
  }

  /**
   * Sets a timeout. When this timeout is reached, the system process executing LoLA is terminated.
   *
   * @param timeout the value for the timeout in seconds
   * @return this
   */
  public LoLaChecker timeout(final int timeout) {
    this.timeout = timeout;
    return this;
  }

  /**
   * Executes LoLA.
   *
   * @return this
   * @throws IOException
   * @throws InterruptedException
   * @throws TimeoutException
   */
  public LoLaChecker check() throws IOException, InterruptedException, TimeoutException {
    // reset result
    result = null;

    // temp file where LoLa can write its output to
    final File output = File.createTempFile("lola", ".json");

    // add default arguments
    parameter(
        "search",
        "cover"); // use coverability graph instead of reachability graph (depth-first search may
    // fail if petri-net is unbounded)
    parameter("encoder", "full"); // required with --search=cover
    // argument("cycle");
    parameter("json", output.getAbsolutePath());
    parameter("jsoninclude", "path");
    parameter("jsoninclude", "state");
    parameter("quiet");

    final List<String> command = Lists.newArrayList("lola");
    command.add(input.getAbsolutePath());
    command.addAll(arguments);

    // execute LoLa
    new ProcessExecutor()
        .command(command)
        .redirectOutput(System.out)
        .destroyOnExit()
        .timeout(timeout, TimeUnit.SECONDS)
        .execute();

    // parse LoLa output
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(LoLaResult.class, new LoLaResultDeserializer())
            .create();
    result = gson.fromJson(new FileReader(output), LoLaResult.class);

    return this;
  }

  /**
   * The result produced LoLA. TRUE = satisfied, FALSE = not satisfied.
   *
   * @return the result of the analysis
   */
  public boolean getResult() {
    return result.getAnalysis().isResult();
  }
}
