package de.monticore.workflow.conformance;

import de.monticore.bpmn.cocos.flow.SequenceFlowNodeReferencesExist;
import de.monticore.bpmn.conformance.datastructures.WfNodeFactory;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.trafos.*;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow.WorkflowTool;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.monticore.bpmn.workflow._parser.WorkflowParser;
import de.monticore.bpmn.workflow._symboltable.WorkflowSTCompleter;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractConfTest {


  protected static final String MODEL_DIR = "src/test/resources/";

    public void init() {
    Log.init();
    WorkflowMill.init();
    WorkflowMill.globalScope().clear();
    BasicSymbolsMill.initializePrimitives();
  }

  /**
   * Parses a model and ensures that the root node is present.
   *
   * @param qualifiedModelName the fully qualified name of the model.
   * @return the root of the parsed model.
   */
  protected ASTWorkflowCompilationUnit loadModel(String qualifiedModelName) {
    WorkflowTool tool = new WorkflowTool();
    ASTWorkflowCompilationUnit ast =
        tool.parse(
            MODEL_DIR
                + Names.getPathFromPackage(qualifiedModelName).replaceAll("\\\\", "/")
                + ".wfm");

    return checkModel(ast);
  }

  public ASTWorkflowCompilationUnit checkModel(ASTWorkflowCompilationUnit ast) {
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    WorkflowCoCoChecker checker = new WorkflowCoCoChecker();
    checker.addCoCo(new SequenceFlowNodeReferencesExist());
    checker.checkAll(ast);
    new AddNameToInlineFlowNodes().transform(ast);
    new AddSequenceFlowToFlowNodes().transform(ast);
    new AddReferenceToParentLane().transform(ast);
    new SetSubProcessTriggeredByEvent().transform(ast);

    WorkflowSTCompleter stCompleter = new WorkflowSTCompleter();
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(stCompleter);
    ast.accept(traverser);

    // getChecker().checkAll(ast);
    return ast;
  }

  public ASTWorkflowCompilationUnit parse_str(String input) {
    WorkflowParser parser = new WorkflowParser();
    Optional<ASTWorkflowCompilationUnit> ast = Optional.empty();
    try {
      ast = parser.parse_String(input);
    } catch (IOException e) {
      Log.error("Error while parsing workflow", e);
    }

    if (ast.isEmpty()) {
      Log.error("Error while parsing workflow");
      assert false;
    }
    return checkModel(ast.get());
  }

  protected List<WfNode> resolveNodeFormBuilder(List<String> nodeNames, WfBuilder builder) {
    List<WfNode> res = new ArrayList<>();

    for (String name : nodeNames) {
      builder.getWfNode(name);
      WfNode node = builder.getWfNode(name);
      res.add(node);
    }
    return res;
  }

  protected WfBuilder parseAndCreateBuilder(String model) {

    ASTWorkflowCompilationUnit ast = loadModel(model);

    return WfNodeFactory.workflowBuilder(ast, "");
  }
}
