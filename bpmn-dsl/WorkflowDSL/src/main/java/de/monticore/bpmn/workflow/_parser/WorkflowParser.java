 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.workflow._parser;

import com.google.common.io.Files;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class WorkflowParser extends WorkflowParserTOP {

  /**
   * Besides parsing, this also checks that the filename equals the model name and the package
   * declaration equals the suffix of the package name of the model.
   */
  /**
   * Parses the model file and checks that the process name matches the filename and that the
   * package declaration matches the location of the model file (relative to the model path root).
   *
   * @param filename the filename
   * @return Optional containing the AST if parsed successfully, xor else an empty Optional
   * @throws IOException
   */
  @Override
  public Optional<ASTWorkflowCompilationUnit> parse(String filename) throws IOException {
    Optional<ASTWorkflowCompilationUnit> optAst = super.parse(filename);

    if (optAst.isPresent()) {
      ASTWorkflowCompilationUnit ast = optAst.get();
      // Use pathName instead of filename (because of correct separators)
      String pathName = Paths.get(filename).toString();

      String simpleFileName = Files.getNameWithoutExtension(pathName);
      String modelName = ast.getWFProcess().getName();

      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));

      if (!modelName.equals(simpleFileName)) {
        Log.error(Messages.get("0xWFM1001", modelName, simpleFileName));
      }

      if (ast.isPresentMCPackageDeclaration()) {
        String packageDeclaration = ast.getMCPackageDeclaration().getMCQualifiedName().getQName();

        if (!packageName.endsWith(packageDeclaration)) {
          Log.error(Messages.get("0xWFM1002", packageDeclaration));
        }
      }
    }

    return optAst;
  }
}
