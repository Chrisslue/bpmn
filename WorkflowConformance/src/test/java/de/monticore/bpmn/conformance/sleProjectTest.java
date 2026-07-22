/*·(c)·https://github.com/MontiCore/monticore·*/
package de.monticore.bpmn.conformance;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbols2Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class sleProjectTest extends AbstractConfTest {
  
  @Test
  void testTest() throws IOException {
    String modelPath = "src/test/resources/de/monticore/bpmn/conformance/sleProject/";
    CD4CodeMill.init();
    var parser = CD4CodeMill.parser();
    ASTCDCompilationUnit cd = parser.parse(modelPath + "TimeManagement.cd").orElseThrow();
    var symbols = CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    CDBasisSymbols2Json symbols2Json = new CDBasisSymbols2Json();
    
    symbols2Json.store(symbols, modelPath + "TimeManagement.cdsym");
    //        loadBPMN("de.monticore.bpmn.conformance.sleProject.TimeManagement", false);
  }
}
