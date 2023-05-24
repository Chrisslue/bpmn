package de.monticore.bpmn.cli.commands;

import static de.monticore.bpmn.cli.commands.MainCommand.OCL_DEFAULT_TYPES_IMPORT;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import de.monticore.bpmn.NamesHelper;
import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.xml.WorkflowXmlSerializer;
import de.monticore.bpmn.xml.WorkflowXmlSerializerVisitor;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

/**
 * The {@code export} checks the context conditions, and exports a BPMN model into the BPMN 2.0 XML
 * exchange format.
 */
@CommandLine.Command(
    name = "export",
    description = "Parse BPMN model and generate BPMN 2.0 XML.",
    mixinStandardHelpOptions = true)
class GenerateXmlCommand implements Runnable {

  private static final String DEFAULT_OUT = "out/";

  /*    @CommandLine.Mixin
  private CommonCliOptions options;*/

  @CommandLine.ParentCommand private MainCommand parent;

  @CommandLine.Option(names = "--check", negatable = true, description = "Skip all checks.")
  boolean check = true;

  @CommandLine.Option(
      names = {"-o", "--out-dir"},
      paramLabel = "DIR",
      description = "Output directory, defaults to " + DEFAULT_OUT)
  private Path outDir = Paths.get(DEFAULT_OUT).toAbsolutePath();

  @Override
  public void run() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(parent.verbose ? Level.ALL : Level.INFO);

    WorkflowTool tool = new WorkflowTool();

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
    new AddReferenceToParentLane().transform(ast);
    new CreateIOSpecification().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);
    if (check) {
      WorkflowCoCos.getFullChecker().checkAll(ast);
    }
    Path outPath = outDir.resolve(Names.getPackageFromPath(parent.qualifiedModel));

    String xmlFileName = Joiners.DOT.join(NamesHelper.getXmlFileName(ast), "bpmn");
    File xmlFile = new File(outPath.toFile(), xmlFileName);
    // ensure that output directory exist
    outPath.toFile().mkdirs();

    ASTProcess process = ast.getProcess();

    try {
      new WorkflowXmlSerializer(ast, new WorkflowXmlSerializerVisitor(process))
          .makeXml()
          .writeToFile(xmlFile);
    } catch (jakarta.xml.bind.JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
