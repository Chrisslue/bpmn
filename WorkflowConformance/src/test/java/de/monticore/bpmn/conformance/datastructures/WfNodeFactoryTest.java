/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.datastructures;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.AbstractConfTest;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WfNodeFactoryTest extends AbstractConfTest {
  
  private final String modelDir = "de.monticore.bpmn.conformance.";
  
  @BeforeEach
  public void setup() {
    init();
    Log.init();
  }
  
  /*
   * The process model to check is the following:
   *
   *        AND1 ->  T3 ;
   *   S -> AND1 ->  T1 ;
   *   P -> AND1 ->  AND2 ->  T2 ;
   *                 AND2 ->  T4 ;
   */
  @Test
  public void testBuilderConstruction() {
    
    WfBuilder builder = parseAndCreateBuilder(modelDir + "datastructures.Example");
    Assertions.assertNotNull(builder);
    
    WfNode s = builder.getWfNode("S");
    WfNode p = builder.getWfNode("P");
    WfNode and1 = builder.getWfNode("AND1");
    WfNode and2 = builder.getWfNode("AND2");
    WfNode t1 = builder.getWfNode("T1");
    WfNode t2 = builder.getWfNode("T2");
    WfNode t3 = builder.getWfNode("T3");
    WfNode t4 = builder.getWfNode("T4");
    
    // checking node S
    Assertions.assertTrue(s.isStart());
    Assertions.assertEquals(1, s.getSuccessors().size());
    Assertions.assertTrue(s.getSuccessors().contains(and1));
    Assertions.assertEquals(0, s.getPredecessors().size());
    
    // checking node P
    Assertions.assertTrue(p.isStart());
    Assertions.assertEquals(1, p.getSuccessors().size());
    Assertions.assertTrue(p.getSuccessors().contains(and1));
    Assertions.assertEquals(0, p.getPredecessors().size());
    
    // checking node AND1
    Assertions.assertEquals(3, and1.getSuccessors().size());
    Assertions.assertTrue(and1.getSuccessors().contains(and2));
    Assertions.assertTrue(and1.getSuccessors().contains(t1));
    Assertions.assertTrue(and1.getSuccessors().contains(t3));
    Assertions.assertEquals(2, and1.getPredecessors().size());
    Assertions.assertTrue(and1.getPredecessors().contains(s));
    Assertions.assertTrue(and1.getPredecessors().contains(p));
    
    // checking node AND2
    Assertions.assertEquals(2, and2.getSuccessors().size());
    Assertions.assertTrue(and2.getSuccessors().contains(t4));
    Assertions.assertTrue(and2.getSuccessors().contains(t2));
    Assertions.assertEquals(1, and2.getPredecessors().size());
    Assertions.assertTrue(and2.getPredecessors().contains(and1));
    
    // checking node T3
    Assertions.assertEquals(0, t3.getSuccessors().size());
    Assertions.assertEquals(1, t3.getPredecessors().size());
    Assertions.assertTrue(t3.getPredecessors().contains(and1));
    
    // checking node T4
    Assertions.assertEquals(0, t4.getSuccessors().size());
    Assertions.assertEquals(1, t4.getPredecessors().size());
    Assertions.assertTrue(t4.getPredecessors().contains(and2));
  }
  
}
