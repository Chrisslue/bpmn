package de.monticore.bpmn.tool;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._symboltable.IWorkflowArtifactScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;


public class BPMNWorkflowTool extends WorkflowTool {

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

            if (cmd.hasOption("v")){
                printVersion();
                return;
            }

            if(!cmd.hasOption("i")){
                Log.error("0xA010 The arguments for the tool should include the option -i");
            }

            String file = cmd.getOptionValue("i");

            ASTWorkflowCompilationUnit model = parse(file);
            IWorkflowArtifactScope as = createSymbolTable(model);
            runDefaultCoCos(model);

            if(cmd.hasOption("pp")){
                prettyPrint(model, cmd.getOptionValue("pp"));
            }

        } catch (ParseException e) {
            // e.getMessage displays the incorrect input-parameters
            Log.error("0xA5C06x68980 Could not process WorkflowTool parameters: " + e.getMessage());

        }

    }

    @Override
    public void runDefaultCoCos(ASTWorkflowCompilationUnit ast) {
        WorkflowCoCoChecker checker = WorkflowCoCos.getFullChecker();
        checker.checkAll(ast);
    }

    @Override
    public void prettyPrint(ASTWorkflowCompilationUnit ast, String file) {
        String ppFile = file + ast.getWFProcess().getName() + ".wfm";
        String model = WorkflowMill.prettyPrint(ast, true);
        print(model, ppFile);
    }



}
