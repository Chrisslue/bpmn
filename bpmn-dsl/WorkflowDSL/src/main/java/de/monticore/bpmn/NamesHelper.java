package de.monticore.bpmn;

import com.google.common.base.CaseFormat;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;

/** Utilities for file names. */
public class NamesHelper {

  public static String getXmlFileName(final ASTWorkflowCompilationUnit ast) {
    return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, ast.getWFProcess().getName());
  }
}
