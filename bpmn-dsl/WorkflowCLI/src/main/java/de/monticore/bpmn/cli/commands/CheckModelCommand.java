package de.monticore.bpmn.cli.commands;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.lang.WorkflowTool;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.bpmn.cli.commands.MainCommand.OCL_DEFAULT_TYPES_IMPORT;

/**
 * The {@code verify} checks the context conditions for a BPMN model.
 */
@Command(
        name = "verify",
        description = "Parse BPMN model and check context conditions.",
        mixinStandardHelpOptions = true
)
class CheckModelCommand implements Runnable {

    private static final String DEFAULT_AUX_OUT = "aux/";

/*    @CommandLine.Mixin
    private CommonCliOptions options;*/

    @CommandLine.ParentCommand
    private
    MainCommand parent;

    @CommandLine.Option(names = "--syntax-only", description = "Run basic checks, skip structural and behaviroal checks.")
    boolean skipExtendedCheck;

    @CommandLine.Option(names = {"-a", "--write-aux"}, description = "Write auxiliary models.")
    boolean printAux;

    @CommandLine.Option(names = {"-o", "--aux-dir"}, paramLabel = "DIR",
            description = "Output directory, defaults to " + DEFAULT_AUX_OUT)
    private Path auxDir = Paths.get(DEFAULT_AUX_OUT).toAbsolutePath();

    @Override
    public void run() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(parent.verbose ? Level.ALL : Level.INFO);

        WorkflowTool tool = new WorkflowTool();

        WorkflowCoCoChecker checker = skipExtendedCheck ? WorkflowCoCos.getBasicChecker() : WorkflowCoCos.getFullChecker();
        Log.enableFailQuick(false);

        tool.addImport(OCL_DEFAULT_TYPES_IMPORT)
                .loadModel(parent.qualifiedModel, parent.modelPath)
                .checkCoCos(checker);

        if (printAux) {
            Path outPath = auxDir.resolve(parent.qualifiedModel.getPackagePath());
            try {
                tool.writeAuxiliaryModels(outPath);
            } catch (final IOException e) {
                Log.error("Failed to write auxiliary models.", e);
            }
        }
    }

}
