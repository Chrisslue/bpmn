/*·(c)·https://github.com/MontiCore/monticore·*/
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.cocos.WorkflowCoCos;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class sleProjectTest extends AbstractConfTest {
  
  void loadCD(){
    String folder = "target/cd2pojo/test/symbols/de/monticore/bpmn/conformance/sleProject/TimeManagement/";
    
    var symbolPaths = CD4AnalysisMill.globalScope().getSymbolPath();
    symbolPaths.addEntry(Path.of(folder + "src/test/resources/"));
    
  }
  
  @Test
  void testTest() throws IOException {
    init();
    loadCD();
    
//    var parser = WorkflowMill.parser();
//    var astroot = parser.parse("src/test/resources/de/monticore/bpmn/conformance/sleProject/TimeManagement.wfm");
//    assertTrue(astroot.isPresent());
//    assertFalse(parser.hasErrors());
    var astroot2 = loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", true);
//    WorkflowMill.scopesGenitorDelegator().createFromAST(astroot.get());
//    WorkflowCoCos.getBasicChecker().checkAll(astroot.get());
  }
}
