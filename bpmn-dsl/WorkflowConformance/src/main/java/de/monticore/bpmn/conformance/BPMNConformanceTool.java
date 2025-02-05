package de.monticore.bpmn.conformance; /* (c) https://github.com/MontiCore/monticore */

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import org.apache.commons.cli.*;

public class BPMNConformanceTool extends WorkflowTool {

  public static void main(String[] args) {
    WorkflowTool tool = new WorkflowTool();
    tool.run(args);
  }

  @Override
  public void init() {
    super.init();
    WorkflowMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
  }

  /**
   * Processes user input from the command line and delegates to the corresponding tools.
   *
   * @param args The input parameters for configuring the BPMNConformance tool.
   */
  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();

    try {
      // create CLI parser and parse input options from command line
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      // help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        return;
      }

      if (!cmd.hasOption("r")) {
        Log.error(
            "BPMN Conformance checking require a reference model, please use option '-r' to introduce a reference model.");
      }

      if (!cmd.hasOption("c")) {
        Log.error(
            "BPMN Conformance checking require a concrete model, please use option '-c' to introduce a reference model.");
      }

      // declared artifacts
      File concreteFile = new File(cmd.getOptionValue("c"));
      File referenceFile = new File(cmd.getOptionValue("r"));

      // given
      ASTWorkflowCompilationUnit reference =
          BPMNConformanceUtils.loadBPMN(referenceFile.getAbsolutePath().split("\\.")[0]);
      ASTWorkflowCompilationUnit concrete =
          BPMNConformanceUtils.loadBPMN(concreteFile.getAbsolutePath().split("\\.")[0]);

      // when
      WfConformanceChecker checker = new WfConformanceChecker();
      checker.checkConformance(concrete, reference, "ref");

    } catch (ParseException e) {
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }
  }

  /**
   * Initializes the standard options for the BPMNConformance tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addStandardOptions(Options options) {

    // help dialog
    Option help = new Option("h", "Prints this help dialog");
    help.setLongOpt("help");
    options.addOption(help);

    return options;
  }

  /**
   * Initializes the additional options for the BPMNConformance tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addAdditionalOptions(Options options) {

    // introduce the reference BPMN
    Option reference =
        Option.builder("r")
            .longOpt("reference")
            .desc("Introduce the Reference BPMN")
            .numberOfArgs(1)
            .build();
    options.addOption(reference);

    // introduce the reference BPMN
    Option concrete =
        Option.builder("c")
            .longOpt("concrete")
            .desc("Introduce the Concrete BPMN")
            .numberOfArgs(1)
            .build();
    options.addOption(concrete);

    return options;
  }
}
