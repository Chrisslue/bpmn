/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.tool;

import de.monticore.bpmn.AbstractTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BPMNWorkflowToolTest extends AbstractTest {
  
  @Test
  public void testTool() {
    BPMNWorkflowTool.main(new String[] { "-i",
        "src/test/resources/de/monticore/bpmn/tool/BPMNWorkflowToolTest.wfm" });
  }
  
  @Test
  public void testToolWithPrettyPrint() {
    //ToDo: add pretty print test
  }
  
}
