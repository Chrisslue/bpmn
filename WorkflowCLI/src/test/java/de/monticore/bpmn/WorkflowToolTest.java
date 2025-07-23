/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WorkflowToolTest extends AbstractToolTest {
  
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
        + "examples/order/OrderToDeliveryWorkflow.wfm", "-path", MODEL_DIR + "examples/order",
        "-pp", TARGET_DIR };
    WorkflowTool.main(conformance);
    //FIXME: Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
