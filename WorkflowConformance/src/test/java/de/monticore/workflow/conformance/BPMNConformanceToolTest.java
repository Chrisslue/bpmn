/* (c) https://github.com/MontiCore/monticore */
package de.monticore.workflow.conformance;

import static org.junit.jupiter.api.Assertions.*;

import de.monticore.bpmn.conformance.BPMNConformanceTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BPMNConformanceToolTest {
  
  private String concrete;
  
  private String reference;
  
  @BeforeEach
  public void setup() {
    String path = "src/test/resources/de/monticore/workflow/conformance/caseStudy/";
    
    concrete = path + "PaperAuthoring.wfm";
    reference = path + "nonconform/AntiPatternMerge.wfm";
  }
  
  @Test
  public void testPossibleCommand() {
    // given
    String[] conformance = new String[] { "-r", reference, "-c", concrete, "-m", "ref" };
    
    // when
    BPMNConformanceTool oclTool = new BPMNConformanceTool();
    oclTool.run(conformance);
  }
  
}
