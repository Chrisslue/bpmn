/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

public class WorkflowToolTest extends AbstractToolTest {
  
  @Test
  public void testConformance() {
    String[] conformance = new String[] { "-i", MODEL_DIR + "Thesis.wfm", "-r", MODEL_DIR
        + "PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    assert Log.getFindings().isEmpty();
  }
  
  @Test
  public void testNonConformance() {
    String[] conformance = new String[] { "-i", MODEL_DIR + "AntiPatternMerge.wfm", "-r", MODEL_DIR
        + "PaperAuthoring.wfm", "-m", "ref" };
    WorkflowTool.main(conformance);
    assert Log.getFindings().isEmpty();
  }
  
}
