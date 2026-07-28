/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WorkflowToolTest {
  
  protected static final String MODEL_DIR = "src/test/resources/de/monticore/bpmn/";
  protected static final String TARGET_DIR = "target/test/de/monticore/bpmn/";
  
  @BeforeEach
  public void setup() {
    Log.init();
    Log.getFindings().clear();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testConformance() {
    String[] conformance = new String[] { "-i", MODEL_DIR + "conformance/Thesis.wfm", "-ref",
        MODEL_DIR + "conformance/PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNonConformance() {
    String[] conformance =
        new String[] { "-i", MODEL_DIR + "conformance/AntiPatternMerge.wfm", "-ref",
            MODEL_DIR + "conformance/PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPrettyPrint() {
    String[] conformance =
        new String[] { "-i", MODEL_DIR + "examples/OrderToDeliveryWorkflow.wfm", "-path",
            "target/cd2pojo/test/symbols/", "-pp", TARGET_DIR + "examples/OrderToDeliveryWorkflow.wfm" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPrettyPrintToStdOut() {
    String[] conformance =
        new String[] { "-i", MODEL_DIR + "examples/OrderToDeliveryWorkflow.wfm", "-path",
            "target/cd2pojo/test/symbols/", "-pp" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStoreSymbolTable() {
    String[] conformance =
        new String[] { "-i", MODEL_DIR + "examples/OrderToDeliveryWorkflow.wfm", "-path",
            "target/cd2pojo/test/symbols/", "-s", TARGET_DIR + "examples/OrderToDeliveryWorkflow.wfsym" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStoreSymbolTable2() {
    String[] conformance = new String[] { "-i", MODEL_DIR + "examples/TimeManagement.wfm", "-path",
        "target/cd2pojo/test/symbols/", "-s", TARGET_DIR + "examples/TimeManagement.wfsym" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
