/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CD4AParserTest {
  
  @Test
  void testVacation() throws IOException {
    CD4CodeMill.init();
    CD4CodeParser parser = CD4CodeMill.parser();
    Path model = Paths.get("src/test/resources/de/monticore/bpmn/cds/Domain.cd");
    Optional<ASTCDCompilationUnit> root = parser.parseCDCompilationUnit(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(root.isPresent());
  }
  
}
