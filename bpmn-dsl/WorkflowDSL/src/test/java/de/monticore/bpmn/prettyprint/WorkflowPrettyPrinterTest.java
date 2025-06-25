 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.prettyprint;

import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import java.io.IOException;
import java.util.Optional;

import de.monticore.bpmn.workflow._parser.WorkflowParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




public class WorkflowPrettyPrinterTest extends AbstractTest {

  
  @Test
  void prettyPrintTest() throws IOException {
    String modelName = "de.monticore.bpmn.prettyprint.PrettyPrintTest";
    ASTWorkflowCompilationUnit cu = parseModel(modelName);
    final ASTWorkflowCompilationUnit ast = parseModel(modelName);
    Assertions.assertNotNull(ast);
    
    // when
    String output = WorkflowMill.prettyPrint(ast, false);
    
    // then
    WorkflowParser parser = WorkflowMill.parser();
    final Optional<ASTWorkflowCompilationUnit> astPrint = parser.parse_String(output);
    Assertions.assertTrue( astPrint.isPresent());
    Assertions.assertTrue(ast.deepEquals(astPrint.get()));
  }
  
 
  
}
