 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.analysis.apt;

import de.monticore.bpmn.analysis.petrinet.PetriNetAptPrinter;
import de.monticore.bpmn.analysis.petrinet.WorkflowNet;
import de.monticore.bpmn.utils.FileUtils;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.zeroturnaround.exec.ProcessExecutor;

/**
 * Interface for APT Petri net tool.
 *
 * <p>https://github.com/CvO-Theory/apt
 */
public class AptChecker {

  private final Path APT_PATH = Paths.get("../lib/apt.jar");

  public void check(final ASTWFProcess process) {
    final WorkflowNet wfNet = WorkflowNet.from(process);

    final String apt =
        new PetriNetAptPrinter().print(wfNet.getPetriNet(), WorkflowNet.initialMarking(wfNet));

    try {
      final String prefix = Long.toString(System.nanoTime());
      final File aptFile = FileUtils.createTempFile(prefix, "apt");
      FileUtils.writeToFile(aptFile, apt);

      final String output =
          new ProcessExecutor()
              .command(
                  "java",
                  "-jar",
                  APT_PATH.toFile().getAbsolutePath(),
                  "strongly_live",
                  aptFile.getAbsolutePath())
              .destroyOnExit()
              .readOutput(true)
              .timeout(20, TimeUnit.SECONDS)
              .execute()
              .outputUTF8();
    } catch (TimeoutException e) {
      Log.warn("Checking formula for '" + process.getName() + "' took too long. Skipping.");
    } catch (Exception e) {
      Log.error("Error while checking formula for '" + process.getName() + "'", e);
    }
  }
}
