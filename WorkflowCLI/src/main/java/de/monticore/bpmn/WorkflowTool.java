/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.conformance.WfConformanceChecker;
import de.monticore.bpmn.trafos.AddNameToInlineFlowNodes;
import de.monticore.bpmn.trafos.AddSequenceFlowToFlowNodes;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

public class WorkflowTool extends de.monticore.bpmn.workflow.WorkflowTool {
  
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
      
      if (cmd.hasOption("v")) {
        printVersion();
        return;
      }
      
      if (!cmd.hasOption("i")) {
        Log.error("0xA010 The arguments for the tool should include the option -i");
      }
      
      String file = cmd.getOptionValue("i");
      ASTWorkflowCompilationUnit model = loadModel(file);
      
      if (cmd.hasOption("r")) {
        String refFile = cmd.getOptionValue("r");
        ASTWorkflowCompilationUnit reference = loadModel(refFile);
        
        // when
        WfConformanceChecker checker = new WfConformanceChecker();
        
        String mapping = "incarnates";
        if (cmd.hasOption("m")) {
          mapping = cmd.getOptionValue("m");
        }
        checker.checkConformance(model, reference, mapping);
      }
      
      if (cmd.hasOption("pp")) {
        prettyPrint(model, cmd.getOptionValue("pp"));
      }
      
    }
    catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C06x68980 Could not process WorkflowTool parameters: " + e.getMessage());
      
    }
  }
  
  @Override
  public void runDefaultCoCos(ASTWorkflowCompilationUnit ast) {
    WorkflowCoCoChecker checker = WorkflowCoCos.getBasicChecker();
    checker.checkAll(ast);
  }
  
  @Override
  public void prettyPrint(ASTWorkflowCompilationUnit ast, String file) {
    String ppFile = file + ast.getWFProcess().getName() + ".wfm";
    String model = WorkflowMill.prettyPrint(ast, true);
    print(model, ppFile);
  }
  
  /**
   * Initializes the additional options for the BPMNConformance tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addAdditionalOptions(Options options) {
    
    // introduce the reference BPMN
    Option reference = Option.builder("r").longOpt("reference").desc(
        "Checks whether the input model conforms to the specified reference model!").numberOfArgs(1)
        .build();
    options.addOption(reference);
    
    // introduce the reference BPMN
    Option map = Option.builder("m").optionalArg(true).longOpt("map").desc(
        "Specifies the name of the incarnation mapping.").numberOfArgs(1).build();
    options.addOption(map);
    
    return options;
  }
  
  protected ASTWorkflowCompilationUnit loadModel(String file) {
    ASTWorkflowCompilationUnit model = parse(file);
    WorkflowMill.scopesGenitorDelegator().createFromAST(model);
    
    new AddNameToInlineFlowNodes().transform(model);
    new AddSequenceFlowToFlowNodes().transform(model);
    
    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    model.accept(traverser);
    
    runDefaultCoCos(model);
    
    return model;
  }
  
}
