/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.conformance.datastructures.WfNodeFactory;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

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
  protected ASTWorkflowCompilationUnit loadBPMN(String qualifiedModelName, boolean checkCoCos) {
    
    return BPMNConformanceUtils.loadBPMN(MODEL_DIR + qualifiedModelName, checkCoCos);
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
    
    ASTWorkflowCompilationUnit ast = loadBPMN(model, false);
    
    return WfNodeFactory.workflowBuilder(ast, "");
  }
  
}
