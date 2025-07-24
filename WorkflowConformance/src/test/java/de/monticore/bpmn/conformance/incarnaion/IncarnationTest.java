/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.incarnaion;

import static de.monticore.bpmn.conformance.BPMNConformanceUtils.parseBPMNString;

import de.monticore.bpmn.conformance.datastructures.WfNodeFactory;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.ConfUtils;
import de.monticore.bpmn.conformance.incarnation.ComposedIncStrategy;
import de.monticore.bpmn.conformance.incarnation.NameIncStrategy;
import de.monticore.bpmn.conformance.incarnation.StereotypesIncStrategy;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.conformance.AbstractConfTest;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IncarnationTest extends AbstractConfTest {
  
  ASTWorkflowCompilationUnit concrete;
  ASTWorkflowCompilationUnit reference;
  
  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }
  
  @Test
  public void testNameIncarnationStrategy() {
    // given
    concrete = parseBPMNString("process Concrete { start event S; task T1; S -> T1;}");
    reference = parseBPMNString("process Reference { start event S; task T1; S -> T1;}");
    
    // when
    WfBuilder conBuilder = WfNodeFactory.workflowBuilder(concrete, "");
    WfBuilder refBuilder = WfNodeFactory.workflowBuilder(reference, "");
    NameIncStrategy incStrategy = new NameIncStrategy(refBuilder);
    
    // then
    List<WfNode> references;
    references = incStrategy.getReferenceElements(conBuilder.getWfNode("S"));
    
    Assertions.assertEquals(1, references.size());
    Assertions.assertEquals("S", references.get(0).getLabel());
  }
  
  @Test
  public void testStereotypeIncarnationStrategy() {
    // given
    concrete = parseBPMNString(
        "process Reference { start event S; <<ref=\"T1\">> task T2; S -> T2;}");
    reference = parseBPMNString("process Concrete { start event S; task T1; S -> T1;}");
    
    // when
    WfBuilder conBuilder = WfNodeFactory.workflowBuilder(concrete, ConfUtils.CONCRETE_PREFIX);
    WfBuilder refBuilder = WfNodeFactory.workflowBuilder(reference, ConfUtils.REFERENCE_PREFIX);
    StereotypesIncStrategy incStrategy = new StereotypesIncStrategy(refBuilder, "ref");
    
    // then
    List<WfNode> references;
    
    // node T1 is incarnate by T2
    references = incStrategy.getReferenceElements(conBuilder.getWfNode("T2"));
    Assertions.assertEquals(1, references.size());
    Assertions.assertEquals("Ref:T1", references.get(0).getLabel());
    
    // node S has no incarnation
    references = incStrategy.getReferenceElements(conBuilder.getWfNode("S"));
    Assertions.assertEquals(0, references.size());
  }
  
  @Test
  public void testComposedIncarnationStrategy() {
    // given
    concrete = parseBPMNString(
        "process Reference { start event S; <<ref=\"T1\">> task T2; S -> T2;}");
    reference = parseBPMNString("process Concrete { start event S; task T1; S -> T1;}");
    
    // when
    WfBuilder conBuilder = WfNodeFactory.workflowBuilder(concrete, "");
    WfBuilder refBuilder = WfNodeFactory.workflowBuilder(reference, "");
    ComposedIncStrategy incStrategy = new ComposedIncStrategy(refBuilder, "ref");
    incStrategy.addIncStrategy(new StereotypesIncStrategy(refBuilder, "ref"));
    incStrategy.addIncStrategy(new NameIncStrategy(refBuilder));
    // then
    List<WfNode> references;
    
    // node T1 is incarnate by T2
    references = incStrategy.getReferenceElements(conBuilder.getWfNode("T2"));
    Assertions.assertEquals(1, references.size());
    Assertions.assertEquals("T1", references.get(0).getLabel());
    
    // node S has no incarnation
    references = incStrategy.getReferenceElements(conBuilder.getWfNode("S"));
    Assertions.assertEquals(1, references.size());
    Assertions.assertEquals("S", references.get(0).getLabel());
  }
  
}
