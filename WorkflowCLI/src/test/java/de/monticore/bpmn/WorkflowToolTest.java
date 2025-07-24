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
    String[] conformance = new String[] { "-i", MODEL_DIR + "Thesis.wfm", "-r", MODEL_DIR
        + "PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNonConformance() {
    String[] conformance = new String[] { "-i", MODEL_DIR + "AntiPatternMerge.wfm", "-r", MODEL_DIR
        + "PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPrettyPrint() {
    String[] conformance = new String[] { "-i", MODEL_DIR
        + "examples/order/OrderToDeliveryWorkflow.wfm", "-path", "src/test/resources", "-pp",
        TARGET_DIR };
    WorkflowTool.main(conformance);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
