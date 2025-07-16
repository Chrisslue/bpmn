/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cli.commands;

import static de.monticore.bpmn.cli.commands.MainCommand.OCL_DEFAULT_TYPES_IMPORT;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** The {@code verify} checks the context conditions for a BPMN model. */
@Command(name = "verify", description = "Parse BPMN model and check context conditions.", mixinStandardHelpOptions = true)
class CheckModelCommand implements Runnable {
  
  private static final String DEFAULT_AUX_OUT = "aux/";
  
  /*    @CommandLine.Mixin
  private CommonCliOptions options;*/
  
  @CommandLine.ParentCommand
  private MainCommand parent;
  
  @CommandLine.Option(names = "--syntax-only", description = "Run basic checks, skip structural and behaviroal checks.")
  boolean skipExtendedCheck;
  
  @CommandLine.Option(names = { "-a", "--write-aux" }, description = "Write auxiliary models.")
  boolean printAux;
  
  @CommandLine.Option(names = { "-o",
      "--aux-dir" }, paramLabel = "DIR", description = "Output directory, defaults to "
          + DEFAULT_AUX_OUT)
  private Path auxDir = Paths.get(DEFAULT_AUX_OUT).toAbsolutePath();
  
  @Override
  public void run() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(parent.verbose ? Level.ALL : Level.INFO);
    
    WorkflowTool tool = new WorkflowTool();
    
    WorkflowCoCoChecker checker = skipExtendedCheck ? WorkflowCoCos.getBasicChecker()
        : WorkflowCoCos.getFullChecker();
    Log.enableFailQuick(false);
    
    Optional<URL> model = parent.modelPath.find(Names.getPathFromPackage(parent.qualifiedModel));
    if (model.isEmpty()) {
      root.error("0xWFM0002 Model file " + parent.qualifiedModel + " does not exist.");
      return;
    }
    ASTWorkflowCompilationUnit ast = tool.parse(model.get().getPath());
    
    new AddMoreImports(Lists.newArrayList(OCL_DEFAULT_TYPES_IMPORT)).transform(ast);
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    WorkflowCoCoChecker beforeChecker = new WorkflowCoCoChecker();
    beforeChecker.addCoCo(new SequenceFlowNodeReferencesExist());
    beforeChecker.checkAll(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    
    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);
    checker.checkAll(ast);
    /*
    if (printAux) {
      Path outPath = auxDir.resolve(Names.getPackageFromPath(parent.qualifiedModel));
      try {
        new AuxiliaryModelsWriter(ast.getProcess())
            .print(outPath.resolve(ast.getProcess().getName().toLowerCase()));
      } catch (final IOException e) {
        Log.error("Failed to write auxiliary models.", e);
      }
    }
    
     */
  }
  
}
