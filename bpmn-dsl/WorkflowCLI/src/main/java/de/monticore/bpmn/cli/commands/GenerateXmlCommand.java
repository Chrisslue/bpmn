package de.monticore.bpmn.cli.commands;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.lang.WorkflowTool;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.bpmn.cli.commands.MainCommand.OCL_DEFAULT_TYPES_IMPORT;

/**
 * The {@code export} checks the context conditions, and exports a BPMN model into the BPMN 2.0 XML exchange format.
 */
@CommandLine.Command(
        name = "export",
        description = "Parse BPMN model and generate BPMN 2.0 XML.",
        mixinStandardHelpOptions = true
)
class GenerateXmlCommand implements Runnable {

    private static final String DEFAULT_OUT = "out/";

/*    @CommandLine.Mixin
    private CommonCliOptions options;*/

    @CommandLine.ParentCommand
    private
    MainCommand parent;

    @CommandLine.Option(names = "--check", negatable = true, description = "Skip all checks.")
    boolean check = true;

    @CommandLine.Option(names = {"-o", "--out-dir"}, paramLabel = "DIR",
            description = "Output directory, defaults to " + DEFAULT_OUT)
    private Path outDir = Paths.get(DEFAULT_OUT).toAbsolutePath();

    @Override
    public void run() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(parent.verbose ? Level.ALL : Level.INFO);

        WorkflowTool tool = new WorkflowTool();

        tool.addImport(OCL_DEFAULT_TYPES_IMPORT)
                .loadModel(parent.qualifiedModel, parent.modelPath);
        if (check) {
            tool.checkCoCos(WorkflowCoCos.getFullChecker());
        }
        Path outPath = outDir.resolve(parent.qualifiedModel.getPackagePath());

        tool.exportXml(outPath);
    }

}
