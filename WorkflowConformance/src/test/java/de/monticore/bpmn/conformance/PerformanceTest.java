/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class PerformanceTest extends AbstractConfTest {
  
  private final String modelDir = "de.monticore.bpmn.conformance.";
  
  @BeforeEach
  public void setup() {
    init();
    LogStub.init();
  }
  
  @Disabled
  @Test
  public void checkMotivatingExample() {
    // given
    
    PerformanceWFMBuilder builder = new PerformanceWFMBuilder();
    for (int i = 1; i <= 10; i++) {
      Optional<ASTWorkflowCompilationUnit> reference = builder.buildWFM(i, false);
      Optional<ASTWorkflowCompilationUnit> concrete = builder.buildWFM(i, true);
      
      Assertions.assertTrue(reference.isPresent());
      Assertions.assertTrue(concrete.isPresent());
      
      BPMNConformanceUtils.completeModel(reference.get());
      BPMNConformanceUtils.completeModel(concrete.get());
      
      // when
      WfConformanceChecker checker = new WfConformanceChecker();
      
      long start = System.currentTimeMillis();
      boolean currentResult = checker.checkConformance(concrete.get(), reference.get(), "ref");
      Assertions.assertFalse(currentResult);
      double duration = (double) (System.currentTimeMillis() - start);
      System.out.println("[Performance Test] Case " + i + ": " + duration);
      
    }
  }
  
}
